package io.github.jd1378.otphelper.worker

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import io.github.jd1378.otphelper.data.local.entity.DetectedCode
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.NotificationHelper

@HiltWorker
class CodeDetectedWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val otpHelperDatabase: OtpHelperDatabase,
    private val userSettingsRepository: UserSettingsRepository,
    private val ignoredNotifsRepository: IgnoredNotifsRepository,
) : CoroutineWorker(context, workerParams) {

  companion object {
    const val TAG: String = "CodeDetectedWorker"
  }

  override suspend fun doWork(): Result {

    val packageName = inputData.getString("packageName")
    val notificationId = inputData.getString("notificationId")
    val notificationTag = inputData.getString("notificationTag") ?: ""
    val text = inputData.getString("text")
    val code = inputData.getString("code")

    if (packageName.isNullOrEmpty() ||
        notificationId.isNullOrEmpty() ||
        text.isNullOrEmpty() ||
        code.isNullOrEmpty()) {
      Log.e(TAG, "Work was missing the necessary work input data. aborting.")
      return Result.failure()
    }

    val settings = userSettingsRepository.fetchSettings()

    val historyId =
        if (!settings.isHistoryDisabled) {
          otpHelperDatabase
              .detectedCodeDao()
              .insert(
                  DetectedCode(
                      packageName = packageName,
                      notificationId = notificationId,
                      notificationTag = notificationTag,
                      text =
                          if (settings.shouldReplaceCodeInHistory)
                              text.replace(code, "0".repeat(code.length))
                          else text,
                  ))
        } else {
          0L
        }

    try {
      if (!ignoredNotifsRepository.isIgnored(
          packageName = packageName,
          notificationId = notificationId,
          notificationTag = notificationTag)) {

        if (settings.isAutoCopyEnabled) {
          Clipboard.copyCodeToClipboard(applicationContext, code, settings.isCopiedToastEnabled)
        }
        if (settings.isPostNotifEnabled) {
          val extras = Bundle()
          extras.putLong("historyId", historyId)
          extras.putString("code", code)
          extras.putString("packageName", packageName)
          extras.putString("notificationId", notificationId)
          extras.putString("notificationTag", notificationTag)

          NotificationHelper.sendDetectedNotif(
              applicationContext, extras, code, settings.isAutoCopyEnabled)
        }
      }
    } catch (e: Exception) {
      Log.e(TAG, e.stackTraceToString())
    }

    return Result.success()
  }
}
