package io.github.jd1378.otphelper

import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
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

    fun isNotificationListenerServiceEnabled(context: Context): Boolean {
      return NotificationManagerCompat.getEnabledListenerPackages(context)
          .contains(context.packageName)
    }

    fun tryReEnableNotificationListener(context: Context) {
      if (isNotificationListenerServiceEnabled(context)) {
        // Rebind the service if it's already enabled
        val componentName =
            ComponentName(
                context,
                NotificationListener::class.java,
            )
        val pm = context.packageManager
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

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    return START_STICKY
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    autoUpdatingListenerUtils.awaitCodeExtractor()
    val codeExtractor = autoUpdatingListenerUtils.codeExtractor!!

    if (sbn != null) {
      val mNotification = sbn.notification
      if (sbn.packageName == BuildConfig.APPLICATION_ID && sbn.id == R.id.code_detected_notify_id)
          return

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
      val notificationText = codeExtractor.cleanup(notifyTexts.toString())

      if (notificationText.isNotEmpty()) {
        val code = codeExtractor.getCode(notificationText, false) // to not do it more than once
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

          if (autoUpdatingListenerUtils.isAutoMarkAsReadEnabled) {
            val actions = mNotification.actions
            if (actions != null) {
              for (action in mNotification.actions) {
                val isReadAction =
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P &&
                        action.semanticAction == Notification.Action.SEMANTIC_ACTION_MARK_AS_READ) {
                      true
                    } else {
                      val title = action.title.toString().lowercase()
                      title.contains("mark") && title.contains("read")
                    }
                if (isReadAction) {
                  try {
                    action.actionIntent.send()
                  } catch (e: Throwable) {
                    Log.d(TAG, "failed to use notification action '${action.title}'")
                  }
                }
              }
            }
          }

          if (autoUpdatingListenerUtils.isAutoDismissEnabled) {
            cancelNotification(sbn.key)
          }
        }
      }
    }
  }

  override fun onListenerDisconnected() {
    super.onListenerDisconnected()

    // Handle the listener disconnected event
    Log.i(TAG, "Notification listener disconnected.")

    if (isNotificationListenerServiceEnabled(applicationContext)) {
      Log.d(TAG, "Rebinding to the service")
      val componentName =
          ComponentName(
              this,
              NotificationListener::class.java,
          )
      requestRebind(componentName)
    }
  }
}
