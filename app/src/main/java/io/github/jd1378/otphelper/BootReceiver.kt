package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.AppLogger
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

  @Inject lateinit var userSettingsRepository: UserSettingsRepository

  override fun onReceive(context: Context, intent: Intent?) {
    AppLogger.i("BootReceiver", "onReceive: action=${intent?.action}")
    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      AppLogger.i("BootReceiver", "boot completed -> rebinding listeners")
      MyWorkManager.rebindListeners(context)
    }
  }
}
