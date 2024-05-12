package io.github.jd1378.otphelper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import io.github.jd1378.otphelper.utils.NotificationHelper

class BootReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent?) {
    if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
      if (context.checkSelfPermission(
          android.Manifest.permission.BIND_NOTIFICATION_LISTENER_SERVICE,
      ) == PackageManager.PERMISSION_GRANTED) {
        context.startService(Intent(context, NotificationListener::class.java))
      } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.R) {
        // the bug is only present in android 11 and before it seems
        NotificationHelper.sendPermissionRevokedNotif(context)
      }
    }
  }
}
