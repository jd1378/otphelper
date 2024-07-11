package io.github.jd1378.otphelper

import android.app.Notification
import android.content.ComponentName
import android.provider.Settings
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.di.AutoUpdatingListenerUtils
import io.github.jd1378.otphelper.worker.CodeDetectedWorker
import javax.inject.Inject

@AndroidEntryPoint
class NotificationListener : NotificationListenerService() {

  @Inject lateinit var autoUpdatingListenerUtils: AutoUpdatingListenerUtils

  companion object {
    val TAG = "NotificationListener"
    val notification_text_keys =
        listOf(
            // Notification.EXTRA_TITLE, // removed due to causing false positives.
            Notification.EXTRA_TEXT,
            Notification.EXTRA_SUB_TEXT,
            Notification.EXTRA_INFO_TEXT,
            Notification.EXTRA_SUMMARY_TEXT,
            Notification.EXTRA_BIG_TEXT,
            Notification.EXTRA_TEXT_LINES,
        )
    val notification_text_arrays_keys = listOf(Notification.EXTRA_TEXT_LINES)
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    if (autoUpdatingListenerUtils.codeExtractor == null) return

    val codeExtractor = autoUpdatingListenerUtils.codeExtractor!!

    if (sbn != null) {
      val mNotification = sbn.notification
      // ignore notifications that are foreground service
      val isForegroundService = (mNotification.flags and Notification.FLAG_FOREGROUND_SERVICE) != 0
      val isOngoing = (mNotification.flags and Notification.FLAG_ONGOING_EVENT) != 0

      if (isForegroundService || isOngoing) return

      val extras = mNotification.extras
      val notifyTexts = StringBuilder()
      for (key in notification_text_keys) {
        val str = extras.getCharSequence(key)?.toString()
        if (!str.isNullOrEmpty()) {
          if (codeExtractor.shouldIgnore(str)) return
          notifyTexts.append(str)
          notifyTexts.append("\n")
        }
      }
      for (key in notification_text_arrays_keys) {
        val array = extras.getCharSequenceArray(key)
        if (array != null) {
          for (charSeq in array) {
            notifyTexts.append(charSeq.toString())
            notifyTexts.append("\n")
          }
        }
      }
      val notificationText = notifyTexts.toString()

      if (notificationText.isNotEmpty()) {
        val code = codeExtractor.getCode(notificationText)
        if (!code.isNullOrEmpty()) {
          val data =
              workDataOf(
                  "packageName" to sbn.packageName,
                  "notificationId" to sbn.id.toString(),
                  "notificationTag" to sbn.tag,
                  "text" to notificationText,
                  "code" to code,
              )
          val work = OneTimeWorkRequestBuilder<CodeDetectedWorker>().setInputData(data).build()
          WorkManager.getInstance(applicationContext).enqueue(work)
        }
      }
    }
  }

  override fun onListenerDisconnected() {
    super.onListenerDisconnected()

    // Handle the listener disconnected event
    Log.i(TAG, "Notification listener disconnected.")

    if (isNotificationServiceEnabled()) {
      Log.d(TAG, "Rebinding to the service")
      val componentName =
          ComponentName(
              this,
              NotificationListener::class.java,
          )
      requestRebind(componentName)
    }
  }

  private fun isNotificationServiceEnabled(): Boolean {
    val pkgName = packageName
    val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
    if (!flat.isNullOrEmpty()) {
      val names = flat.split(":").toTypedArray()
      for (name in names) {
        val componentName = ComponentName.unflattenFromString(name)
        if (componentName != null) {
          if (pkgName == componentName.packageName) {
            return true
          }
        }
      }
    }
    return false
  }
}
