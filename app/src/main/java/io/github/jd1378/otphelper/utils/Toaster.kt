package io.github.jd1378.otphelper.utils

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.widget.Toast

// Shows a toast on the main thread. Use Toast.LENGTH_LONG only when the text needs
// time to read (e.g. it contains a code); plain confirmations stay short.
fun showToast(context: Context, text: String, duration: Int = Toast.LENGTH_SHORT) {
  Handler(Looper.getMainLooper()).post { Toast.makeText(context, text, duration).show() }
}
