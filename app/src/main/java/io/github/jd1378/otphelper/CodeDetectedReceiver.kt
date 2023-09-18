package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.data.IgnoredNotifSetRepository
import io.github.jd1378.otphelper.data.SettingsRepository
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.NotificationSender
import javax.inject.Inject
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CodeDetectedReceiver : BroadcastReceiver() {

  @Inject lateinit var ignoredNotifSetRepository: IgnoredNotifSetRepository
  @Inject lateinit var settingsRepository: SettingsRepository

  companion object {
    const val INTENT_ACTION_CODE_DETECTED = "io.github.jd1378.otphelper.code_detected"
    const val INTENT_ACTION_CODE_DETECTED_PERMISSION =
        "io.github.jd1378.otphelper.permission.RECEIVE_CODE"
    const val NOTIFICATION_TIMEOUT = 60_000L

    const val TAG = "CodeDetectedReceiver"
  }

  override fun onReceive(context: Context?, intent: Intent?) {
    if (intent?.extras !== null && context !== null) {
      var intentIgnoreWord = intent.extras!!.getString("ignore_word")

      @OptIn(DelicateCoroutinesApi::class)
      GlobalScope.launch() {
        try {
          if (intentIgnoreWord != null) {
            if (ignoredNotifSetRepository.hasIgnoredNotif(intentIgnoreWord)) {
              return@launch
            }
          }

          val code = intent.extras!!.getString("code")
          if (code.isNullOrEmpty()) return@launch

          val autoCopyEnabled = settingsRepository.getIsAutoCopyEnabled()
          if (autoCopyEnabled) {
            Clipboard.copyCodeToClipboard(context, code)
          }
          if (settingsRepository.getIsPostNotifEnabled()) {
            NotificationSender.sendDetectedNotif(context, intent.extras!!, code, autoCopyEnabled)
          }
        } catch (e: Exception) {
          Log.e(TAG, e.stackTraceToString())
        }
      }
    }
  }
}
