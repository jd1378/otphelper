package io.github.jd1378.otphelper.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter

fun drawableToBitmap(drawable: Drawable): Bitmap {
  if (drawable is BitmapDrawable) {
    return drawable.bitmap
  }

  val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth else 1
  val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight else 1
  val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
  val canvas = Canvas(bitmap)
  drawable.setBounds(0, 0, canvas.width, canvas.height)
  drawable.draw(canvas)
  return bitmap
}

fun bitmapToPainter(bitmap: Bitmap): BitmapPainter {
  return BitmapPainter(bitmap.asImageBitmap())
}
