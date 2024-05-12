package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationManagerCompat
import io.github.jd1378.otphelper.utils.NotificationHelper

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      if (NotificationManagerCompat.getEnabledListenerPackages(context)
          .contains(context.packageName)) {
        context.startService(Intent(context, NotificationListener::class.java))
      } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
        // the bug is only present in android 11 and before it seems
        NotificationHelper.sendPermissionRevokedNotif(context)
      }
    }
  }
}
