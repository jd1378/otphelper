package io.github.jd1378.otphelper.utils

import android.content.Context
import android.widget.Toast
import androidx.compose.ui.platform.UriHandler
import io.github.jd1378.otphelper.R

/**
 * Opens the given uri, showing a toast instead of crashing when no activity can handle it (e.g. no
 * browser installed).
 */
fun UriHandler.openUriSafely(context: Context, uri: String) {
  try {
    openUri(uri)
  } catch (e: Exception) {
    Toast.makeText(context, R.string.failed_to_open_link, Toast.LENGTH_SHORT).show()
  }
}
