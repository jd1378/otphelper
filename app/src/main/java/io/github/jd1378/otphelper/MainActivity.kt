package io.github.jd1378.otphelper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.MyWorkManager.doDataMigration
import io.github.jd1378.otphelper.MyWorkManager.enableHistoryCleanup
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.ActivityHelper
import io.github.jd1378.otphelper.utils.SettingsHelper
import kotlinx.coroutines.launch
import javax.inject.Inject

const val INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS =
    "INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject lateinit var userSettingsRepository: UserSettingsRepository
  @Inject lateinit var deepLinkHandler: DeepLinkHandler

  companion object {
    const val scale = 1.15f
  }

  override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(ActivityHelper.adjustFontSize(newBase, scale))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    installSplashScreen()
    super.onCreate(savedInstanceState)
    ActivityHelper.adjustFontSize(this, scale)

    if (intent.action == INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS) {
      SettingsHelper.openNotificationListenerSettings(this)
    }

    lifecycleScope.launch {
      val settings = userSettingsRepository.fetchSettings()

      // setup initial settings
      if (!settings.isMigrationDone) {
        doDataMigration(applicationContext)
        enableHistoryCleanup(applicationContext)
      }
    }

    setContent { OtpHelperApp(deepLinkHandler) }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    if (intent.action == INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS) {
      SettingsHelper.openNotificationListenerSettings(this)
    } else {
      deepLinkHandler.handleDeepLink(intent)
    }
  }

  override fun onStart() {
    super.onStart()
    if (intent.action == INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS) {
      SettingsHelper.openNotificationListenerSettings(this)
    } else {
      deepLinkHandler.handleDeepLink(intent)
    }
    // consume the deeplink
    intent = null
  }
}
