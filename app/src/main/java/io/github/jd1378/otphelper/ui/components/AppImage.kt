package io.github.jd1378.otphelper.ui.components

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.jd1378.otphelper.utils.bitmapToPainter
import io.github.jd1378.otphelper.utils.drawableToBitmap

@Composable
fun AppImage(packageName: String, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  var drawable: Drawable? = null
  var appLabel: String? = null

  try {
    val packageManager = context.packageManager
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    drawable = packageManager.getApplicationIcon(applicationInfo)
    appLabel = packageManager.getApplicationLabel(applicationInfo).toString()
  } catch (e: Exception) {
    e.printStackTrace()
  }

  drawable?.let {
    val bitmap = drawableToBitmap(it)
    val painter = bitmapToPainter(bitmap)
    Image(painter = painter, contentDescription = appLabel, modifier = modifier)
  }
}

@Preview(showBackground = true)
@Composable
fun AppImagePreview() {
  AppImage(packageName = "io.github.jd1378.otphelper", modifier = Modifier.size(64.dp))
}
