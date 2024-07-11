package io.github.jd1378.otphelper.ui.components

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.utils.bitmapToPainter
import io.github.jd1378.otphelper.utils.drawableToBitmap

@Immutable data class AppInfoResult(val icon: Drawable, val appLabel: String, val failed: Boolean)

fun AppInfoResult.shortenAppLabel(length: Int = 40): String {
  return this.appLabel.substring(0, length) + "..."
}

fun getAppInfo(context: Context, packageName: String?): AppInfoResult {
  return try {
    if (packageName.isNullOrEmpty()) error("packageName is null")
    val packageManager = context.packageManager
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    AppInfoResult(
        packageManager.getApplicationIcon(applicationInfo),
        packageManager.getApplicationLabel(applicationInfo).toString(),
        false)
  } catch (e: Exception) {
    AppInfoResult(
        AppCompatResources.getDrawable(context, R.drawable.baseline_question_mark_24)!!,
        packageName ?: "",
        true)
  }
}

@Composable
fun AppImage(icon: Drawable, modifier: Modifier = Modifier) {
  val bitmap = drawableToBitmap(icon)
  val painter = bitmapToPainter(bitmap)
  Image(painter = painter, contentDescription = null, modifier = modifier)
}
