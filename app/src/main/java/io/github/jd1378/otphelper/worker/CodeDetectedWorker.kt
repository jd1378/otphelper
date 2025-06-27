package io.github.jd1378.otphelper.worker

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import io.github.jd1378.otphelper.data.local.entity.DetectedCode
import io.github.jd1378.otphelper.di.DETECTION_LOCK
import io.github.jd1378.otphelper.di.DETECTION_TIMEOUT_MS
import io.github.jd1378.otphelper.di.RecentDetectedMessageHolder
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.Clipboard
import io.github.jd1378.otphelper.utils.NotificationHelper
import kotlinx.coroutines.delay

@HiltWorker
class CodeDetectedWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val otpHelperDatabase: OtpHelperDatabase,
    private val userSettingsRepository: UserSettingsRepository,
    private val ignoredNotifsRepository: IgnoredNotifsRepository,
    private var recentDetectedMessageHolder: RecentDetectedMessageHolder
) : CoroutineWorker(context, workerParams) {

  companion object {
    const val TAG: String = "CodeDetectedWorker"
  }

  override suspend fun doWork(): Result {

    var packageName = inputData.getString("packageName")
    val smsOrigin = inputData.getString("smsOrigin") ?: ""
    val notificationId = inputData.getString("notificationId") ?: ""
    val notificationTag = inputData.getString("notificationTag") ?: ""
    var text = inputData.getString("text")
    var code = inputData.getString("code")
    val isSms = inputData.getBoolean("is_sms", false)

    val missingData =
        when {
          packageName.isNullOrBlank() -> true
          text.isNullOrBlank() -> true
          code.isNullOrBlank() -> true
          isSms && smsOrigin.isBlank() -> true
          !isSms && notificationId.isBlank() -> true
          else -> false
        }
    if (missingData) {
      Log.e(TAG, "Work was missing the necessary work input data. aborting.")
      return Result.failure()
    }
    // necessary because smart cast didnt detect the when clause correctly:
    packageName = packageName!!
    code = code!!
    text = text!!

    val isIgnored =
        ignoredNotifsRepository.isIgnored(
            packageName = packageName,
            notificationId = notificationId,
            notificationTag = notificationTag,
            smsOrigin = smsOrigin)

    if (isIgnored) return Result.success()

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
                      smsOrigin = smsOrigin,
                      text =
                          if (settings.shouldReplaceCodeInHistory)
                              text.replace(code, "0".repeat(code.length))
                          else text,
                  ))
        } else {
          0L
        }

    try {
      if (settings.isShowToastEnabled) {
        Handler(Looper.getMainLooper()).post {
          Toast.makeText(
                  applicationContext,
                  applicationContext.getString(R.string.detected_code) + " " + code,
                  Toast.LENGTH_SHORT)
              .show()
        }
      }
      if (settings.isAutoCopyEnabled) {
        Clipboard.copyCodeToClipboard(
            applicationContext,
            code,
            settings.isShowCopyConfirmationEnabled && !settings.isShowToastEnabled,
            !settings.isCopyAsNotSensitiveEnabled)
      }
      if (settings.isPostNotifEnabled) {
        val extras = Bundle()
        extras.putLong("historyId", historyId)
        extras.putString("code", code)
        extras.putString("packageName", packageName)
        extras.putBoolean("is_sms", isSms)
        if (isSms) {
          extras.putString("smsOrigin", smsOrigin)
        } else {
          extras.putString("notificationId", notificationId)
          extras.putString("notificationTag", notificationTag)
        }

        NotificationHelper.sendDetectedNotif(
            applicationContext, extras, code, settings.isAutoCopyEnabled)
      }
    } catch (e: Exception) {
      Log.e(TAG, e.stackTraceToString())
    }

    // cleanup recentDetectedMessageHolder after timeout
    delay(DETECTION_TIMEOUT_MS)
    synchronized(DETECTION_LOCK) {
      val message = recentDetectedMessageHolder.message
      if (message != null) {
        if (System.currentTimeMillis() - message.timestamp >= DETECTION_TIMEOUT_MS) {
          // if the timeout has elapsed / no new message is replaced while we waited
          // we clean it up
          recentDetectedMessageHolder.message = null
        }
      }
    }

    return Result.success()
  }
}
