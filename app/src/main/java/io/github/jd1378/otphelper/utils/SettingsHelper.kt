package io.github.jd1378.otphelper.utils

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import io.github.jd1378.otphelper.NotificationListener

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
  }
}
