package io.github.jd1378.otphelper

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.ActivityHelper
import io.github.jd1378.otphelper.utils.SettingsHelper
import io.github.jd1378.otphelper.worker.MigrateWorker
import io.github.jd1378.otphelper.worker.migrateWorkName
import kotlinx.coroutines.launch
import javax.inject.Inject

const val INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS =
    "INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS"

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

  @Inject lateinit var userSettingsRepository: UserSettingsRepository

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
        val workRequest =
            OneTimeWorkRequestBuilder<MigrateWorker>()
                .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
                .build()
        WorkManager.getInstance(applicationContext)
            .enqueueUniqueWork(
                migrateWorkName,
                ExistingWorkPolicy.KEEP,
                workRequest,
            )
      }
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
