package io.github.jd1378.otphelper.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.PendingIntentCompat
import io.github.jd1378.otphelper.CodeDetectedReceiver
import io.github.jd1378.otphelper.NotifActionReceiver
import io.github.jd1378.otphelper.R
import java.util.Date

class NotificationSender {
  companion object {
    private fun hasNotifPermission(context: Context): Boolean {
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
      } else {
        true
      }
    }

    private fun createDetectedChannel(context: Context): String {
      val channelId = context.getString(R.string.code_detected_channel_id)
      // Create the NotificationChannel only on API 26+
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.code_detected_channel_name)
        val descriptionText = context.getString(R.string.code_detected_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel =
            NotificationChannel(channelId, name, importance).apply { description = descriptionText }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
      }
      return channelId
    }

    @SuppressLint("MissingPermission", "LaunchActivityFromNotification")
    fun sendDetectedNotif(
        context: Context,
        extras: Bundle,
        code: String,
        copied: Boolean = false,
    ) {
      if (!hasNotifPermission(context)) return

      val notificationRV = RemoteViews(context.packageName, R.layout.code_notification_countdown)
      notificationRV.setTextViewText(
          R.id.code_detected_label, context.getString(R.string.detected_code))
      notificationRV.setTextViewText(R.id.detected_code, code)

      if (copied) {
        notificationRV.setImageViewIcon(
            R.id.action_image, Icon.createWithResource(context, R.drawable.baseline_check_24))
        notificationRV.setViewVisibility(R.id.copied_textview, View.VISIBLE)
      }

      var channelId = createDetectedChannel(context)

      var copyIntent = Intent(NotifActionReceiver.INTENT_ACTION_CODE_COPY)
      copyIntent.setPackage(context.packageName)
      val copyPendingIntent = PendingIntentCompat.getBroadcast(context, 0, copyIntent, 0, false)

      var ignoreIntent = Intent(NotifActionReceiver.INTENT_ACTION_IGNORE_NOTIFICATION)
      ignoreIntent.setPackage(context.packageName)
      ignoreIntent.putExtra("cancel_notif_id", R.id.code_detected_notify_id)

      var ignorePendingIntent = PendingIntentCompat.getBroadcast(context, 0, ignoreIntent, 0, false)

      var notification =
          NotificationCompat.Builder(context, channelId)
              .setExtras(extras)
              .setSmallIcon(R.drawable.ic_launcher_foreground)
              .setStyle(NotificationCompat.DecoratedCustomViewStyle())
              .setCustomContentView(notificationRV)
              .setContentIntent(copyPendingIntent)
              .setGroup("code")
              .setUsesChronometer(true)
              .setChronometerCountDown(true)
              .setShowWhen(true)
              .setWhen(Date().time + CodeDetectedReceiver.NOTIFICATION_TIMEOUT)
              .setCategory(Notification.CATEGORY_SERVICE)
              .setSortKey("0")
              .setTimeoutAfter(CodeDetectedReceiver.NOTIFICATION_TIMEOUT)
              .setSilent(true)
              .setVibrate(null)
              .addAction(
                  R.drawable.baseline_content_copy_24,
                  context.getString(R.string.copy),
                  copyPendingIntent)
              .addAction(
                  R.drawable.baseline_visibility_off_24,
                  context.getString(R.string.ignore),
                  ignorePendingIntent)
              .build()

      NotificationManagerCompat.from(context).notify(R.id.code_detected_notify_id, notification)

      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
        // we need to schedule notification cleanup on older devices
        Handler(Looper.getMainLooper())
            .postDelayed(
                { NotificationManagerCompat.from(context).cancel(R.id.code_detected_notify_id) },
                CodeDetectedReceiver.NOTIFICATION_TIMEOUT)
      }
    }
  }
}
