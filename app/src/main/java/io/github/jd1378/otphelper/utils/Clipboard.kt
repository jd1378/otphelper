package io.github.jd1378.otphelper.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipDescription
import android.content.ClipboardManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.widget.Toast
import io.github.jd1378.otphelper.R

class Clipboard {
  companion object {
    /** returns true if successful */
    fun copyCodeToClipboard(context: Context, code: String): Boolean {
      var clipboardManager =
          context.getSystemService(Activity.CLIPBOARD_SERVICE) as? ClipboardManager

      var toastText: Int =
          if (clipboardManager !== null) {
            clipboardManager.setPrimaryClip(
                ClipData.newPlainText(code, code).apply {
                  description.extras =
                      PersistableBundle().apply {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                          putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
                        } else {
                          putBoolean("android.content.extra.IS_SENSITIVE", true)
                        }
                      }
                })
            R.string.code_copied_to_clipboard
          } else {
            R.string.code_failed_to_access_clipboard
          }
      Handler(Looper.getMainLooper()).post {
        Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show()
      }
      return toastText == R.string.code_copied_to_clipboard
    }
  }
}
