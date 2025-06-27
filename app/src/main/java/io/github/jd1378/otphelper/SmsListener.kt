package io.github.jd1378.otphelper

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.di.AutoUpdatingListenerUtils
import io.github.jd1378.otphelper.di.RecentDetectedMessage
import io.github.jd1378.otphelper.di.RecentDetectedMessageHolder
import io.github.jd1378.otphelper.worker.CodeDetectedWorker
import javax.inject.Inject

@AndroidEntryPoint
class SmsListener : BroadcastReceiver() {

  @Inject lateinit var autoUpdatingListenerUtils: AutoUpdatingListenerUtils
  @Inject lateinit var recentDetectedMessageHolder: RecentDetectedMessageHolder

  companion object {
    val TAG = "SmsListener"

    fun hasSmsPermission(context: Context): Boolean {
      return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) ==
          PackageManager.PERMISSION_GRANTED &&
          ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_SMS) ==
              PackageManager.PERMISSION_GRANTED
    }

    fun isSmsListenerServiceEnabled(context: Context): Boolean {
      val componentName = ComponentName(context, SmsListener::class.java)
      val pm = context.packageManager
      val enabledSetting = pm.getComponentEnabledSetting(componentName)
      return when (enabledSetting) {
        PackageManager.COMPONENT_ENABLED_STATE_ENABLED -> true
        PackageManager.COMPONENT_ENABLED_STATE_DEFAULT -> {
          // Default state: check if declared in manifest as enabled
          try {
            val info = pm.getReceiverInfo(componentName, 0)
            info.enabled
          } catch (e: Exception) {
            false
          }
        }
        else -> false
      }
    }

    fun disable(context: Context) {
      context.packageManager.setComponentEnabledSetting(
          ComponentName(
              context,
              SmsListener::class.java,
          ),
          PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
          PackageManager.DONT_KILL_APP,
      )
    }

    fun enable(context: Context) {
      context.packageManager.setComponentEnabledSetting(
          ComponentName(
              context,
              SmsListener::class.java,
          ),
          PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
          PackageManager.DONT_KILL_APP,
      )
    }
  }

  override fun onReceive(context: Context, intent: Intent?) {
    autoUpdatingListenerUtils.awaitCodeExtractor()
    if (autoUpdatingListenerUtils.modeOfOperation != ModeOfOperation.SMS) return
    if (intent?.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
      for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
        val messageBody = smsMessage.messageBody

        if (!messageBody.isNullOrBlank() && !smsMessage.displayOriginatingAddress.isNullOrBlank()) {
          autoUpdatingListenerUtils.awaitCodeExtractor()
          val codeExtractor = autoUpdatingListenerUtils.codeExtractor!!

          val text = codeExtractor.cleanup(messageBody)

          if (text.isNotEmpty()) {
            val code = codeExtractor.getCode(text, false) // to not do it more than once
            if (!code.isNullOrEmpty()) {
              val data: Data
              try {
                data =
                    workDataOf(
                        "packageName" to Telephony.Sms.getDefaultSmsPackage(context),
                        "is_sms" to true,
                        "smsOrigin" to smsMessage.displayOriginatingAddress,
                        "text" to text,
                        "code" to code,
                    )
              } catch (e: Throwable) {
                Log.e(TAG, "Notification too large to check, skipping it...")
                return
              }
              recentDetectedMessageHolder.message =
                  RecentDetectedMessage(messageBody, System.currentTimeMillis())
              val work = OneTimeWorkRequestBuilder<CodeDetectedWorker>().setInputData(data).build()
              WorkManager.getInstance(context).enqueue(work)
            }
          }
        }
      }
    }
  }
}
