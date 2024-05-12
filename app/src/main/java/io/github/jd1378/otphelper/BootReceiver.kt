package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import io.github.jd1378.otphelper.utils.NotificationHelper

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      if (NotificationManagerCompat.getEnabledListenerPackages(context)
          .contains(context.packageName)) {
        context.startService(Intent(context, NotificationListener::class.java))
      } else {
        NotificationHelper.sendPermissionRevokedNotif(context)
      }
    }
  }
}
