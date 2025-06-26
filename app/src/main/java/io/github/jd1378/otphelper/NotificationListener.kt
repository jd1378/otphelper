package io.github.jd1378.otphelper

import android.annotation.SuppressLint
import android.app.Notification
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.di.AutoUpdatingListenerUtils
import io.github.jd1378.otphelper.di.RecentDetectedMessageHolder
import io.github.jd1378.otphelper.worker.CodeDetectedWorker
import javax.inject.Inject

@AndroidEntryPoint
class NotificationListener : NotificationListenerService() {

  @Inject lateinit var autoUpdatingListenerUtils: AutoUpdatingListenerUtils
  @Inject lateinit var recentDetectedMessageHolder: RecentDetectedMessageHolder

  companion object {
    val TAG = "NotificationListener"
    private const val MESSAGE_TIMEOUT_MS = 4_000L
    private val redactedNotificationMessages =
        mutableSetOf(
            "Sensitive notification content hidden", // en
            "محتوای اعلان حساس پنهان شده است", // fa
            "تم إخفاء المحتوى الحساس في الإشعار", // ar
            "已隐藏敏感通知内容", // b+zh+Hans
            "系統已隱藏含有私密資訊的通知內容", // b+zh+Hant
            "Деликатното съдържание в известието е скрито", // bg
            "S'ha amagat contingut sensible de les notificacions", // ca
            "Vertrauliche Benachrichtigungsinhalte ausgeblendet", // de
            "Contenido sensible de la notificación oculto", // es
            "Märguande delikaatne sisu peideti", // et
            "Le contenu sensible de la notification a été masqué", // fr
            "संवेदनशील जानकारी वाली सूचना का कॉन्टेंट छिपा है", // hi
            "Contenuti sensibili della notifica nascosti", // it
            "יש תוכן רגיש בהתראה שהוסתר", // iw
            "プライベートな通知内容は表示されません", // ja
            "민감한 알림 콘텐츠 숨김", // ko
            "Treść poufnego powiadomienia została ukryta", // pl
            "Conteúdo de notificação sensível oculto", // pt
            "Conținutul sensibil din notificări a fost ascuns", // ro
            "Конфиденциальная информация в уведомлении скрыта", // ru
            "உணர்வுபூர்வமான அறிவிப்பு உள்ளடக்கம் மறைக்கப்பட்டது", // ta
            "Hassas bildirim içerikleri gizlendi", // tr
            "Чутливий вміст сповіщення приховано", // uk
            "Đã ẩn nội dung thông báo nhạy cảm", // vi
        )

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

    fun enable(context: Context) {
      context.packageManager.setComponentEnabledSetting(
          ComponentName(
              context,
              NotificationListener::class.java,
          ),
          PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
          PackageManager.DONT_KILL_APP,
      )
    }

    fun disable(context: Context) {
      context.packageManager.setComponentEnabledSetting(
          ComponentName(
              context,
              NotificationListener::class.java,
          ),
          PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
          PackageManager.DONT_KILL_APP,
      )
    }

    @SuppressLint("DiscouragedApi")
    private fun hasRedactedMessage(
        notif: Notification,
    ): Boolean {
      try {
        // we do this every time because system language can be changed at any point in time
        // if we cache it, it can result in incorrect behavior
        val resId =
            Resources.getSystem()
                .getIdentifier("redacted_notification_message", "string", "android")
        val res = Resources.getSystem().getString(resId)
        if (res.isNotBlank()) {
          // just in case future android versions change the message
          redactedNotificationMessages.add(res)
        }
      } catch (e: Throwable) {
        // this is just in case the above api stops working in future versions
      }
      // Check redacted strings
      notif.extras.getCharSequence(Notification.EXTRA_TEXT)?.toString()?.let { str ->
        if (redactedNotificationMessages.contains(str)) return true
      }

      return false
    }
  }

  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    super.onStartCommand(intent, flags, startId)
    return START_STICKY
  }

  override fun onNotificationPosted(sbn: StatusBarNotification?) {
    super.onNotificationPosted(sbn)
    autoUpdatingListenerUtils.awaitCodeExtractor()
    if (autoUpdatingListenerUtils.modeOfOperation != ModeOfOperation.Notification &&
        !autoUpdatingListenerUtils.isAutoDismissEnabled &&
        !autoUpdatingListenerUtils.isAutoMarkAsReadEnabled) {
      return
    }
    if (sbn != null) {
      if (sbn.packageName == BuildConfig.APPLICATION_ID && sbn.id == R.id.code_detected_notify_id)
          return

      val mNotification = sbn.notification
      // ignore notifications that are foreground service
      val isForegroundService = (mNotification.flags and Notification.FLAG_FOREGROUND_SERVICE) != 0
      val isOngoing = (mNotification.flags and Notification.FLAG_ONGOING_EVENT) != 0

      if (isForegroundService || isOngoing) return

      var codeDetected = false

      if (autoUpdatingListenerUtils.modeOfOperation == ModeOfOperation.Notification) {
        val codeExtractor = autoUpdatingListenerUtils.codeExtractor!!

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
            val data: Data
            try {
              data =
                  workDataOf(
                      "packageName" to sbn.packageName,
                      "notificationId" to sbn.id.toString(),
                      "notificationTag" to sbn.tag,
                      "text" to notificationText,
                      "code" to code,
                  )
            } catch (e: Throwable) {
              Log.e(TAG, "Notification too large to check, skipping it...")
              return
            }
            codeDetected = true
            val work = OneTimeWorkRequestBuilder<CodeDetectedWorker>().setInputData(data).build()
            WorkManager.getInstance(applicationContext).enqueue(work)
          }
        }
      } else {
        val message = recentDetectedMessageHolder.message
        // sms mode and have detected message recently
        if (message != null) {
          if (System.currentTimeMillis() - message.timestamp <= MESSAGE_TIMEOUT_MS) {
            // fast detection path: We simply get the normal text of notification and check for
            // access
            // denial text, which means this notification is a sensitive notification
            // so we cannot check further because of restrictions of android 15+, but we it's likely
            // to be the notification we want
            //
            // slow detection path:
            // we try to find the message in the notification text partially, if found, it is the
            // notification we are looking for. we do this lazily and based on what is more likely
            // to
            // contain the target text.
            // we only take 25 characters from the message since it should be more than enough to
            // identify the notification
            //
            // when found, we set codeDetected to true and apply the post detection actions to the
            // notification

            // we just need to check Notification.EXTRA_TEXT for redacted message at first
            codeDetected = hasRedactedMessage(mNotification)

            if (!codeDetected) {
              // now we check if we can see the recent message we have inside the notification texts
              val extras = mNotification.extras
              for (key in notification_text_keys) {
                val str = extras.getCharSequence(key)?.toString()
                if (!str.isNullOrBlank() && str.contains(message.body)) {
                  codeDetected = true
                  break
                }
              }
              if (!codeDetected) {
                for (key in notification_text_arrays_keys) {
                  val array = extras.getCharSequenceArray(key)
                  if (array != null) {
                    for (charSeq in array) {
                      if (!charSeq.isNullOrBlank() && charSeq.contains(message.body)) {
                        codeDetected = true
                        break
                      }
                    }
                  }
                }
              }
            }
          } else {
            // we don't need to check if the detection is too old now
            return
          }
        }
      }

      if (codeDetected) {
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
