package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.NotificationHelper
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CodeDetectedReceiver : BroadcastReceiver() {

  @Inject lateinit var ignoredNotifsRepository: IgnoredNotifsRepository
  @Inject lateinit var userSettingsRepository: UserSettingsRepository

  companion object {
    const val INTENT_ACTION_CODE_DETECTED = "io.github.jd1378.otphelper.code_detected"
    const val INTENT_ACTION_CODE_DETECTED_PERMISSION =
        "io.github.jd1378.otphelper.permission.RECEIVE_CODE"
    const val NOTIFICATION_TIMEOUT = 60_000L
    const val INTENT_EXTRA_PACKAGE_NAME = "package_name"
    const val INTENT_EXTRA_NOTIFICATION_ID = "notification_id"
    const val INTENT_EXTRA_NOTIFICATION_TAG = "notification_tag"

    const val TAG = "CodeDetectedReceiver"
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.extras !== null && context !== null) {
      val notifPackageName = intent.extras!!.getString(INTENT_EXTRA_PACKAGE_NAME)!!
      val notifId = intent.extras!!.getString(INTENT_EXTRA_NOTIFICATION_ID)!!
      val notifTag = intent.extras!!.getString(INTENT_EXTRA_NOTIFICATION_TAG)

      @OptIn(DelicateCoroutinesApi::class)
      GlobalScope.launch() {
        try {
          if (ignoredNotifsRepository.isIgnored(
              packageName = notifPackageName,
              notificationId = notifId,
              notificationTag = notifTag)) {
            return@launch
          }

          val code = intent.extras!!.getString("code")
          if (code.isNullOrEmpty()) return@launch

          val settings = userSettingsRepository.fetchSettings()
          if (settings.isAutoCopyEnabled) {
            Clipboard.copyCodeToClipboard(context, code)
          }
          if (settings.isPostNotifEnabled) {
            NotificationHelper.sendDetectedNotif(
                context, intent.extras!!, code, settings.isAutoCopyEnabled)
          }
        } catch (e: Exception) {
          Log.e(TAG, e.stackTraceToString())
        }
      }
    }
  }
}
