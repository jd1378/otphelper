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
import io.github.reactivecircus.cache4k.Cache
import kotlin.time.Duration.Companion.minutes

@Immutable data class AppInfoResult(val icon: Drawable, val appLabel: String, val failed: Boolean)

fun AppInfoResult.shortenAppLabel(length: Int = 40): String {
  if (length > this.appLabel.length) {
    return this.appLabel
  }
  return this.appLabel.substring(0, length) + "..."
}

val cache = Cache.Builder<String, AppInfoResult>().expireAfterAccess(5.minutes).build()

fun getMissingAppInfoResult(context: Context, packageName: String?): AppInfoResult {
  return AppInfoResult(
      AppCompatResources.getDrawable(context, R.drawable.baseline_question_mark_24)!!,
      packageName ?: "",
      true)
}

fun getAppInfo(context: Context, packageName: String?): AppInfoResult {
  if (packageName.isNullOrEmpty()) getMissingAppInfoResult(context, packageName)
  var result = cache.get(packageName!!)
  if (result == null) {
    result =
        try {
          val packageManager = context.packageManager
          val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
          AppInfoResult(
              packageManager.getApplicationIcon(applicationInfo),
              packageManager.getApplicationLabel(applicationInfo).toString(),
              false)
        } catch (e: Exception) {
          getMissingAppInfoResult(context, packageName)
        }
    cache.put(packageName, result!!)
  }
  return result
}

@Composable
fun AppImage(icon: Drawable, modifier: Modifier = Modifier) {
  val bitmap = drawableToBitmap(icon)
  val painter = bitmapToPainter(bitmap)
  Image(painter = painter, contentDescription = null, modifier = modifier)
}
