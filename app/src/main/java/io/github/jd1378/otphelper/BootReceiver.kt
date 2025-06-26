package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import javax.inject.Inject

@AndroidEntryPoint
class BootReceiver : BroadcastReceiver() {

  @Inject lateinit var userSettingsRepository: UserSettingsRepository

  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      MyWorkManager.rebindListeners(context)
    }
  }
}
