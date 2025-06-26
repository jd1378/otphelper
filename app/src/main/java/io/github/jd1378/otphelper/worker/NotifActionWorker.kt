package io.github.jd1378.otphelper.worker

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.app.NotificationManagerCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jd1378.otphelper.NotifActionReceiver.Companion.INTENT_ACTION_CODE_COPY
import io.github.jd1378.otphelper.NotifActionReceiver.Companion.INTENT_ACTION_IGNORE_NOTIFICATION_APP
import io.github.jd1378.otphelper.NotifActionReceiver.Companion.INTENT_ACTION_IGNORE_SMS_ORIGIN
import io.github.jd1378.otphelper.NotifActionReceiver.Companion.INTENT_ACTION_IGNORE_TAG_NOTIFICATION_NID
import io.github.jd1378.otphelper.NotifActionReceiver.Companion.INTENT_ACTION_IGNORE_TAG_NOTIFICATION_TAG
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.NotificationHelper

@HiltWorker
class NotifActionWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userSettingsRepository: UserSettingsRepository,
    private val ignoredNotifsRepository: IgnoredNotifsRepository,
) : CoroutineWorker(context, workerParams) {

  companion object {
    const val TAG: String = "NotifActionWorker"

    fun getActiveNotification(context: Context, notificationId: Int): Notification? {
      val notificationManager =
          context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
      val barNotifications = notificationManager.activeNotifications
      return barNotifications.firstOrNull { it.id == notificationId }?.notification
    }
  }

  override suspend fun doWork(): Result {
    val action = inputData.getString("action")
    val cancelNotifId = inputData.getInt("cancel_notif_id", -1)
    val context = applicationContext

    val notif = getActiveNotification(context, R.id.code_detected_notify_id)
    if (notif != null) {

      val ignoredPackageName = notif.extras.getString("packageName")!!
      var ignoredType: IgnoredNotifType? = null
      var ignoredTypeData = ""

      when (action) {
        INTENT_ACTION_CODE_COPY -> {
          val code = notif.extras.getString("code")

          if (code != null) {
            val copied =
                Clipboard.copyCodeToClipboard(
                    context,
                    code,
                    userSettingsRepository.fetchSettings().isShowCopyConfirmationEnabled)
            NotificationHelper.sendDetectedNotif(context, notif.extras, code, copied)
          }
        }

        INTENT_ACTION_IGNORE_NOTIFICATION_APP -> {
          ignoredType = IgnoredNotifType.APPLICATION
          sendToast(context, R.string.wont_detect_code_from_this_app)
        }

        INTENT_ACTION_IGNORE_TAG_NOTIFICATION_TAG -> {
          ignoredType = IgnoredNotifType.NOTIFICATION_TAG
          ignoredTypeData = notif.extras.getString("notificationTag", "")
          sendToast(context, R.string.wont_detect_code_from_this_notif)
        }

        INTENT_ACTION_IGNORE_TAG_NOTIFICATION_NID -> {
          ignoredType = IgnoredNotifType.NOTIFICATION_ID
          ignoredTypeData = notif.extras.getString("notificationId", "")
          sendToast(context, R.string.wont_detect_code_from_this_notif)
        }

        INTENT_ACTION_IGNORE_SMS_ORIGIN -> {
          ignoredType = IgnoredNotifType.SMS_ORIGIN
          ignoredTypeData = notif.extras.getString("smsOrigin", "")
          sendToast(context, R.string.wont_detect_code_from_this_origin)
        }
      }

      if (ignoredType != null) {
        ignoredNotifsRepository.setIgnored(
            packageName = ignoredPackageName,
            type = ignoredType,
            typeData = ignoredTypeData,
        )
      }
    }

    if (cancelNotifId != -1) {
      NotificationManagerCompat.from(context).cancel(cancelNotifId)
    }

    return Result.success()
  }

  private fun sendToast(
      context: Context,
      @StringRes message: Int,
  ) {
    val handler = Handler(Looper.getMainLooper())
    handler.post { Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
  }
}
