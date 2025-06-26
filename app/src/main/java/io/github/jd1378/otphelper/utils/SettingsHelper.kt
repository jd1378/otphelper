package io.github.jd1378.otphelper.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import io.github.jd1378.otphelper.NotificationListener
import io.github.jd1378.otphelper.R

class SettingsHelper {
  companion object {
    fun openNotificationListenerSettings(context: Context) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // Go directly to the app's notification listener settings page
        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
        intent.putExtra(
            Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
            ComponentName(
                    context,
                    NotificationListener::class.java,
                )
                .flattenToString(),
        )

        try {
          context.startActivity(intent)
        } catch (e: Exception) {
          // Not all phones had this action in Android 11, this is a fallback
          context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
        }
      } else {
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
      }
    }

    fun openApplicationSettings(context: Context) {
      val intent =
          Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
              .addCategory(Intent.CATEGORY_DEFAULT)
              .setData(Uri.fromParts("package", context.packageName, null))
              .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
              .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
              .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
      try {
        context.startActivity(intent)
      } catch (e: Exception) {
        Toast.makeText(context, R.string.failed_to_open_app_settings, Toast.LENGTH_LONG).show()
      }
    }
  }
}
