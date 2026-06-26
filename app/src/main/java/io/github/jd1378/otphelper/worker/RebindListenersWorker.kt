package io.github.jd1378.otphelper.worker

import android.content.Context
import android.content.Intent
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
import io.github.jd1378.otphelper.utils.AppLogger
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
    AppLogger.i(
        TAG,
        "doWork: setupFinished=${userSettings.isSetupFinished}, " +
            "mode=${userSettings.modeOfOperation}",
    )
    if (!userSettings.isSetupFinished) {
      AppLogger.i(TAG, "setup not finished, nothing to rebind")
      return Result.success()
    }

    val silent = inputData.getBoolean("silent", false)

    if (userSettings.modeOfOperation == ModeOfOperation.SMS) {
      if (hasSmsPermission(applicationContext)) {
        AppLogger.i(TAG, "SMS mode: (re)enabling SmsListener")
        try {
          applicationContext.startService(Intent(applicationContext, SmsListener::class.java))
        } catch (e: Throwable) {
          AppLogger.e(TAG, "Failed to start SmsListener", e)
        }
        SmsListener.disable(applicationContext)
        SmsListener.enable(applicationContext)
      } else if (!silent) {
        AppLogger.w(TAG, "SMS mode but SMS permission revoked, notifying user")
        NotificationHelper.sendSmsPermissionRevokedNotif(applicationContext)
      }
    }

    if (userSettings.modeOfOperation == ModeOfOperation.Notification) {
      SmsListener.disable(applicationContext)
    }

    if (isNotificationListenerServiceEnabled(applicationContext)) {
      AppLogger.i(TAG, "(re)enabling NotificationListener")
      try {
        applicationContext.startService(
            Intent(applicationContext, NotificationListener::class.java))
      } catch (e: Throwable) {
        AppLogger.e(TAG, "Failed to start NotificationListener", e)
      }
      NotificationListener.disable(applicationContext)
      NotificationListener.enable(applicationContext)
    } else if (!silent) {
      AppLogger.w(TAG, "notification listener access not granted, notifying user")
      NotificationHelper.sendPermissionRevokedNotif(applicationContext)
    }
    return Result.success()
  }
}
