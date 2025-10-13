package io.github.jd1378.otphelper

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import dagger.hilt.android.HiltAndroidApp
import io.github.jd1378.otphelper.utils.NotificationHelper.Companion.createNotificationChannels
import javax.inject.Inject

@HiltAndroidApp
class App : Application(), Configuration.Provider {
  @Inject lateinit var workerFactory: HiltWorkerFactory

  override val workManagerConfiguration: Configuration
    get() = Configuration.Builder().setWorkerFactory(workerFactory).build()

  override fun onCreate() {
    super.onCreate()
    createNotificationChannels(applicationContext)
    MyWorkManager.rebindListeners(applicationContext, true)
  }
}
