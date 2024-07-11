package io.github.jd1378.otphelper.ui.components

import android.content.Context
import android.text.TextUtils.substring
import androidx.compose.runtime.Immutable

@Immutable data class AppLabelResult(val label: String, val failed: Boolean)

/** returns the package name if app label is not found. */
fun getAppLabel(context: Context, packageName: String?, trim: Int? = 40): AppLabelResult {
  if (packageName.isNullOrEmpty()) return AppLabelResult("", true)
  try {
    val packageManager = context.packageManager
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    val result =
        AppLabelResult(packageManager.getApplicationLabel(applicationInfo).toString(), false)
    if (trim != null) {
      return if (result.label.length > trim)
          result.copy(label = result.label.substring(0, trim) + "...")
      else result
    }
    return result
  } catch (e: Exception) {
    return AppLabelResult(packageName, true)
  }
}
