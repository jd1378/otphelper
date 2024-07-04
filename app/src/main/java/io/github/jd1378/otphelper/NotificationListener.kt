package io.github.jd1378.otphelper

import android.app.Notification
import android.content.ComponentName
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import io.github.jd1378.otphelper.utils.CodeExtractor
import io.github.jd1378.otphelper.utils.CodeIgnore

class NotificationListener : NotificationListenerService() {
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
          if (CodeIgnore.shouldIgnore(str)) return
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
        val code = CodeExtractor.getCode(notificationText)
        if (code != null) {
          val intent = Intent(CodeDetectedReceiver.INTENT_ACTION_CODE_DETECTED)
          intent.putExtra("code", code)

          intent.putExtra(CodeDetectedReceiver.INTENT_EXTRA_PACKAGE_NAME, sbn.packageName)
          intent.putExtra(CodeDetectedReceiver.INTENT_EXTRA_NOTIFICATION_ID, sbn.id.toString())
          if (sbn.tag?.contains(":") == true) {
            intent.putExtra(CodeDetectedReceiver.INTENT_EXTRA_NOTIFICATION_TAG, sbn.tag)
          }

          sendBroadcast(intent, CodeDetectedReceiver.INTENT_ACTION_CODE_DETECTED_PERMISSION)
        }
      }
    }
  }

  override fun onListenerDisconnected() {
    super.onListenerDisconnected()
    // Handle the listener disconnected event
    Log.i(TAG, "Notification listener disconnected. attempting to rebind.")

    // Request to rebind the service
    val componentName =
        ComponentName(
            this,
            NotificationListener::class.java,
        )
    requestRebind(componentName)
  }
}
