package io.github.jd1378.otphelper

import android.app.Notification
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.NotificationHelper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NotifActionReceiver : BroadcastReceiver() {

  @Inject lateinit var ignoredNotifsRepository: IgnoredNotifsRepository

  companion object {
    const val INTENT_ACTION_CODE_COPY = "io.github.jd1378.otphelper.actions.code_copy"
    const val INTENT_ACTION_IGNORE_TAG_NOTIFICATION_TAG =
        "io.github.jd1378.otphelper.actions.ignore_notif_tag"
    const val INTENT_ACTION_IGNORE_TAG_NOTIFICATION_NID =
        "io.github.jd1378.otphelper.actions.ignore_notif_nid"
    const val INTENT_ACTION_IGNORE_NOTIFICATION_APP =
        "io.github.jd1378.otphelper.actions.ignore_notif_app"
    const val INTENT_ACTION_SEE_DETAILS = "io.github.jd1378.otphelper.actions.see_details"

    fun getActiveNotification(context: Context, notificationId: Int): Notification? {
      val notificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val barNotifications = notificationManager.activeNotifications
      return barNotifications.firstOrNull { it.id == notificationId }?.notification
    }
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (context == null || intent == null) return

    if (intent.action == INTENT_ACTION_CODE_COPY) {
      val notif = getActiveNotification(context, R.id.code_detected_notify_id)
      if (notif != null) {
        val code = notif.extras.getString("code")

        if (code != null) {
          NotificationHelper.sendDetectedNotif(
              context, notif.extras, code, copied = Clipboard.copyCodeToClipboard(context, code))
        }
      }
    }

    val notif = getActiveNotification(context, R.id.code_detected_notify_id)
    if (notif != null) {

      val ignoredPackageName = notif.extras.getString("packageName")!!
      var ignoredType: IgnoredNotifType? = null
      var ignoredTypeData = ""

      when (intent.action) {
        INTENT_ACTION_IGNORE_NOTIFICATION_APP -> {
          ignoredType = IgnoredNotifType.APPLICATION
          Toast.makeText(context, R.string.wont_detect_code_from_this_app, Toast.LENGTH_LONG).show()
        }
        INTENT_ACTION_SEE_DETAILS -> {
          val historyId = notif.extras.getLong("historyId", 0L)
          if (historyId > 0L) {
            val pendingIntent =
                getDeepLinkPendingIntent(
                    context, MainDestinations.HISTORY_DETAIL_ROUTE, historyId.toString())
            pendingIntent.send()
          }
        }
        INTENT_ACTION_IGNORE_TAG_NOTIFICATION_TAG -> {
          ignoredType = IgnoredNotifType.NOTIFICATION_TAG
          ignoredTypeData = notif.extras.getString("notificationTag", "")
          Toast.makeText(context, R.string.wont_detect_code_from_this_notif, Toast.LENGTH_LONG)
              .show()
        }
        INTENT_ACTION_IGNORE_TAG_NOTIFICATION_NID -> {
          ignoredType = IgnoredNotifType.NOTIFICATION_ID
          ignoredTypeData = notif.extras.getString("notificationId", "")
          Toast.makeText(context, R.string.wont_detect_code_from_this_notif, Toast.LENGTH_LONG)
              .show()
        }
      }

      if (ignoredType != null) {
        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch {
          ignoredNotifsRepository.setIgnored(
              packageName = ignoredPackageName, type = ignoredType, typeData = ignoredTypeData)
        }
      }
    }

    val cancelNotifId = intent.getIntExtra("cancel_notif_id", -1)
    if (cancelNotifId != -1) {
      NotificationManagerCompat.from(context).cancel(cancelNotifId)
    }
  }
}
