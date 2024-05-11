package io.github.jd1378.otphelper.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
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
import io.github.jd1378.otphelper.INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS
import io.github.jd1378.otphelper.MainActivity
import io.github.jd1378.otphelper.NotifActionReceiver
import io.github.jd1378.otphelper.R
import java.util.Date

class NotificationHelper {
  companion object {
    private fun hasNotifPermission(context: Context): Boolean {
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
      } else {
        true
      }
    }

    private fun createPermissionRevokedChannel(context: Context): String {
      val channelId = context.getString(R.string.permission_revoked_channel_id)
      // Create the NotificationChannel only on API 26+
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.permission_revoked_channel_name)
        val descriptionText = context.getString(R.string.permission_revoked_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(channelId, name, importance).apply { description = descriptionText }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
      }
      return channelId
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
        notificationRV.setViewVisibility(R.id.copy_textview, View.GONE)
      }

      val channelId = createDetectedChannel(context)

      val copyPendingIntent =
          PendingIntentCompat.getBroadcast(
              context,
              0,
              Intent(NotifActionReceiver.INTENT_ACTION_CODE_COPY).apply {
                setPackage(context.packageName)
              },
              0,
              false,
          )

      val ignoreAppPendingIntent =
          PendingIntentCompat.getBroadcast(
              context,
              0,
              Intent(NotifActionReceiver.INTENT_ACTION_IGNORE_NOTIFICATION_APP).apply {
                setPackage(context.packageName)
                putExtra("cancel_notif_id", R.id.code_detected_notify_id)
              },
              0,
              false,
          )

      val notificationBuilder =
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
                  R.drawable.baseline_visibility_off_24,
                  context.getString(R.string.ignore_app),
                  ignoreAppPendingIntent)

      if (extras.getString(CodeDetectedReceiver.INTENT_EXTRA_IGNORE_TAG) != null) {
        val ignoreTagPendingIntent =
            PendingIntentCompat.getBroadcast(
                context,
                0,
                Intent(NotifActionReceiver.INTENT_ACTION_IGNORE_TAG_NOTIFICATION_TAG).apply {
                  setPackage(context.packageName)
                  putExtra("cancel_notif_id", R.id.code_detected_notify_id)
                },
                0,
                false,
            )

        notificationBuilder.addAction(
            R.drawable.baseline_visibility_off_24,
            context.getString(R.string.ignore_tag),
            ignoreTagPendingIntent)
      }

      NotificationManagerCompat.from(context)
          .notify(R.id.code_detected_notify_id, notificationBuilder.build())

      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
        // we need to schedule notification cleanup on older devices
        Handler(Looper.getMainLooper())
            .postDelayed(
                { NotificationManagerCompat.from(context).cancel(R.id.code_detected_notify_id) },
                CodeDetectedReceiver.NOTIFICATION_TIMEOUT)
      }
    }

    @SuppressLint("MissingPermission")
    fun sendPermissionRevokedNotif(
        context: Context,
    ) {
      if (!hasNotifPermission(context)) return

      val channelId = createPermissionRevokedChannel(context)

      val openSettingsPendingIntent =
          PendingIntentCompat.getActivity(
              context,
              0,
              Intent(context, MainActivity::class.java).apply {
                setPackage(context.packageName)
                setAction(INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS)
                setFlags(
                    Intent.FLAG_ACTIVITY_NO_HISTORY or
                        Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP)
              },
              PendingIntent.FLAG_ONE_SHOT,
              false,
          )

      val notificationBuilder =
          NotificationCompat.Builder(context, channelId)
              .setSmallIcon(R.drawable.ic_launcher_foreground)
              .setStyle(NotificationCompat.DecoratedCustomViewStyle())
              .setContentTitle(context.getString(R.string.permission_revoked))
              .setContentText(context.getString(R.string.permission_revoked_notification_hint))
              .setContentIntent(openSettingsPendingIntent)
              .setCategory(Notification.CATEGORY_ERROR)
              .setSortKey("0")
              .setVibrate(null)
              .setAutoCancel(true)

      NotificationManagerCompat.from(context)
          .notify(R.id.permission_revoked_notify_id, notificationBuilder.build())
    }
  }
}
