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
import io.github.jd1378.otphelper.INTENT_ACTION_OPEN_NOTIFICATION_LISTENER_SETTINGS
import io.github.jd1378.otphelper.MainActivity
import io.github.jd1378.otphelper.NotifActionReceiver
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.getDeepLinkPendingIntent
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import java.util.Date

class NotificationHelper {
  companion object {
    fun hasNotifPermission(context: Context): Boolean {
      return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
      } else {
        true
      }
    }

    fun createNotificationChannels(context: Context) {
      createPermissionRevokedChannel(context)
      createDetectedChannel(context)
    }

    private fun createPermissionRevokedChannel(context: Context): String {
      val channelId = context.getString(R.string.permission_revoked_channel_id)
      // Create the NotificationChannel only on API 26+
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = context.getString(R.string.permission_revoked)
        val descriptionText = context.getString(R.string.permission_revoked_channel_description)
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(channelId, name, importance).apply { description = descriptionText }
        // Register the channel with the system
        val notificationManager = NotificationManagerCompat.from(context)
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
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel =
            NotificationChannel(channelId, name, importance).apply { description = descriptionText }
        // Register the channel with the system
        val notificationManager = NotificationManagerCompat.from(context)
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

      val smsOrigin = extras.getString("smsOrigin") ?: ""
      val isSms = extras.getBoolean("is_sms", false) && smsOrigin.isNotBlank()

      val notificationRV = RemoteViews(context.packageName, R.layout.code_notification_countdown)
      notificationRV.setTextViewText(
          R.id.code_detected_label,
          context.getString(R.string.detected_code),
      )
      notificationRV.setTextViewText(R.id.detected_code, code)

      if (copied) {
        notificationRV.setImageViewIcon(
            R.id.action_image,
            Icon.createWithResource(context, R.drawable.baseline_check_24),
        )
        notificationRV.setViewVisibility(R.id.copied_textview, View.VISIBLE)
        notificationRV.setViewVisibility(R.id.copy_textview, View.GONE)
      }

      val channelId = context.getString(R.string.code_detected_channel_id)

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

      val notificationTimeout = 60_000L

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
              .setWhen(Date().time + notificationTimeout)
              .setCategory(Notification.CATEGORY_SERVICE)
              .setSortKey("0")
              .setTimeoutAfter(notificationTimeout)
              .setSilent(true)
              .setVibrate(null)
      if (isSms) {
        val ignoreOriginPendingIntent =
            PendingIntentCompat.getBroadcast(
                context,
                0,
                Intent(NotifActionReceiver.INTENT_ACTION_IGNORE_SMS_ORIGIN).apply {
                  setPackage(context.packageName)
                  putExtra("cancel_notif_id", R.id.code_detected_notify_id)
                },
                0,
                false,
            )
        notificationBuilder.addAction(
            R.drawable.baseline_visibility_off_24,
            context.getString(R.string.ignore_origin),
            ignoreOriginPendingIntent,
        )
      } else {
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
        notificationBuilder.addAction(
            R.drawable.baseline_visibility_off_24,
            context.getString(R.string.ignore_app),
            ignoreAppPendingIntent,
        )
      }

      val historyId = extras.getLong("historyId", 0L)
      if (historyId > 0L) {
        val openHistoryPendingIntent =
            getDeepLinkPendingIntent(
                context,
                MainDestinations.HISTORY_DETAIL_ROUTE,
                historyId.toString(),
            )
        notificationBuilder.addAction(
            R.drawable.baseline_visibility_off_24,
            context.getString(R.string.show_details),
            openHistoryPendingIntent,
        )
      } else {
        val notificationId = extras.getString("notificationId")
        val notificationTag = extras.getString("notificationTag")

        if (!notificationTag.isNullOrEmpty() && notificationTag.contains(':')) {
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
              ignoreTagPendingIntent,
          )
        } else if (!notificationId.isNullOrEmpty()) {
          val ignoreNidPendingIntent =
              PendingIntentCompat.getBroadcast(
                  context,
                  0,
                  Intent(NotifActionReceiver.INTENT_ACTION_IGNORE_TAG_NOTIFICATION_NID).apply {
                    setPackage(context.packageName)
                    putExtra("cancel_notif_id", R.id.code_detected_notify_id)
                  },
                  0,
                  false,
              )

          notificationBuilder.addAction(
              R.drawable.baseline_visibility_off_24,
              context.getString(R.string.ignore_id),
              ignoreNidPendingIntent,
          )
        }
      }

      NotificationManagerCompat.from(context)
          .notify(R.id.code_detected_notify_id, notificationBuilder.build())

      if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O) {
        // we need to schedule notification cleanup on older devices
        Handler(Looper.getMainLooper())
            .postDelayed(
                { NotificationManagerCompat.from(context).cancel(R.id.code_detected_notify_id) },
                notificationTimeout,
            )
      }
    }

    @SuppressLint("MissingPermission")
    fun sendPermissionRevokedNotif(
        context: Context,
    ) {
      if (!hasNotifPermission(context)) return

      val channelId = context.getString(R.string.permission_revoked_channel_id)

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
                        Intent.FLAG_ACTIVITY_SINGLE_TOP,
                )
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

    fun sendTestNotif(context: Context) {
      if (
          ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
              PackageManager.PERMISSION_GRANTED
      ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

          val name = context.getString(R.string.code_detected_channel_name)
          val descriptionText = context.getString(R.string.code_detected_channel_description)
          val importance = NotificationManager.IMPORTANCE_DEFAULT
          val channel =
              NotificationChannel("test_chan", name, importance).apply {
                description = descriptionText
              }

          // Register the channel with the system
          val notificationManager: NotificationManager =
              context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
          notificationManager.createNotificationChannel(channel)
        }

        val tag =
            "A very long tag to test the ignore behavior and see what happens when a very long tag is shown."

        val notification =
            NotificationCompat.Builder(context, "test_chan")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle(context.getString(R.string.test_notification_title))
                .setContentText(context.getString(R.string.test_notification_content))
                .build()

        NotificationManagerCompat.from(context).notify(tag, 10, notification)
      }
    }

    @SuppressLint("MissingPermission")
    fun sendSmsPermissionRevokedNotif(
        context: Context,
    ) {
      if (!hasNotifPermission(context)) return

      val channelId = context.getString(R.string.permission_revoked_channel_id)

      val openPermissionsPendingIntent =
          getDeepLinkPendingIntent(
              context,
              MainDestinations.PERMISSIONS_ROUTE,
          )

      val notificationBuilder =
          NotificationCompat.Builder(context, channelId)
              .setSmallIcon(R.drawable.ic_launcher_foreground)
              .setStyle(NotificationCompat.DecoratedCustomViewStyle())
              .setContentTitle(context.getString(R.string.permission_revoked))
              .setContentText(context.getString(R.string.permission_revoked_sms_notification_hint))
              .setContentIntent(openPermissionsPendingIntent)
              .setCategory(Notification.CATEGORY_ERROR)
              .setSortKey("0")
              .setVibrate(null)
              .setAutoCancel(true)

      NotificationManagerCompat.from(context)
          .notify(R.id.permission_revoked_notify_id, notificationBuilder.build())
    }
  }
}
