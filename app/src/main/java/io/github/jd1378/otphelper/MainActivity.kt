package io.github.jd1378.otphelper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.utils.ActivityHelper
import io.github.jd1378.otphelper.utils.SettingsHelper

const val INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS =
    "INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  companion object {
    const val scale = 1.15f
  }

  override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(ActivityHelper.adjustFontSize(newBase, scale))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ActivityHelper.adjustFontSize(this, scale)

    if (intent.action == INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS) {
      SettingsHelper.openNotificationListenerSettings(this)
    }

    setContent { OtpHelperApp() }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    if (intent.action == INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS) {
      SettingsHelper.openNotificationListenerSettings(this)
    }
  }
}
