package io.github.jd1378.otphelper.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration

class ActivityHelper {
  companion object {
    @SuppressLint("QueryPermissionsNeeded")
    fun isCallable(context: Context, intent: Intent): Boolean {
      val list =
          context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
      return list.size > 0
    }

    fun adjustFontSize(context: Context, scale: Float = 1.0f): Context {
      val configuration: Configuration = context.resources.configuration
      configuration.fontScale = scale
      return context.createConfigurationContext(configuration)
    }
  }
}
