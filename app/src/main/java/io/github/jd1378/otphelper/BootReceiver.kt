package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import io.github.jd1378.otphelper.NotificationListener.Companion.isNotificationListenerServiceEnabled
import io.github.jd1378.otphelper.NotificationListener.Companion.tryReEnableNotificationListener
import io.github.jd1378.otphelper.utils.NotificationHelper

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      if (isNotificationListenerServiceEnabled(context)) {
        try {
          context.startService(Intent(context, NotificationListener::class.java))
        } catch (e: Throwable) {
          Log.e(
              "BootReceiver",
              "Failed to start NotificationListener",
          )
        }
        tryReEnableNotificationListener(context)
      } else {
        NotificationHelper.sendPermissionRevokedNotif(context)
      }
    }
  }
}
