package io.github.jd1378.otphelper.worker

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jd1378.otphelper.ModeOfOperation
import io.github.jd1378.otphelper.NotificationListener
import io.github.jd1378.otphelper.NotificationListener.Companion.isNotificationListenerServiceEnabled
import io.github.jd1378.otphelper.SmsListener
import io.github.jd1378.otphelper.SmsListener.Companion.hasSmsPermission
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.NotificationHelper

const val rebindListenersWorkName = "rebind_listeners_work"

@HiltWorker
class RebindListenersWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userSettingsRepository: UserSettingsRepository,
) : CoroutineWorker(context, workerParams) {

  companion object {
    const val TAG: String = "RebindListenersWorker"
  }

  override suspend fun doWork(): Result {
    val userSettings = userSettingsRepository.fetchSettings()
    if (!userSettings.isSetupFinished) return Result.success()

    val silent = inputData.getBoolean("silent", false)

    if (userSettings.modeOfOperation == ModeOfOperation.SMS) {
      if (hasSmsPermission(applicationContext)) {
        try {
          applicationContext.startService(Intent(applicationContext, SmsListener::class.java))
        } catch (e: Throwable) {
          Log.e(
              "BootReceiver",
              "Failed to start NotificationListener",
          )
        }
        SmsListener.disable(applicationContext)
        SmsListener.enable(applicationContext)
      } else if (!silent) {
        NotificationHelper.sendSmsPermissionRevokedNotif(applicationContext)
      }
    }

    if (userSettings.modeOfOperation == ModeOfOperation.Notification) {
      SmsListener.disable(applicationContext)
    }

    if (isNotificationListenerServiceEnabled(applicationContext)) {
      try {
        applicationContext.startService(
            Intent(applicationContext, NotificationListener::class.java))
      } catch (e: Throwable) {
        Log.e(
            "BootReceiver",
            "Failed to start NotificationListener",
        )
      }
      NotificationListener.disable(applicationContext)
      NotificationListener.enable(applicationContext)
    } else if (!silent) {
      NotificationHelper.sendPermissionRevokedNotif(applicationContext)
    }
    return Result.success()
  }
}
