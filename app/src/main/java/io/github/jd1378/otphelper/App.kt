package io.github.jd1378.otphelper

import android.app.Application
import android.content.ComponentName
import android.content.pm.PackageManager
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.jd1378.otphelper.NotificationListener.Companion.isNotificationServiceEnabled
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
  @Inject lateinit var workerFactory: HiltWorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

  override fun onCreate() {
    super.onCreate()

    if (isNotificationServiceEnabled(applicationContext)) {
      // Rebind the service if it's already enabled
      val componentName =
          ComponentName(
              this,
              NotificationListener::class.java,
          )
      val pm = packageManager
      pm.setComponentEnabledSetting(
          componentName,
          PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
          PackageManager.DONT_KILL_APP,
      )
      pm.setComponentEnabledSetting(
          componentName,
          PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
          PackageManager.DONT_KILL_APP,
      )
    }
  }
}
