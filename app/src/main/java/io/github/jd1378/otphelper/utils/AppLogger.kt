package io.github.jd1378.otphelper.utils

import android.util.Log

/**
 * Central logger for debugging the app in production.
 *
 * All logs share a single tag ([TAG]) so a tester can capture everything the app does with one
 * filter:
 * ```
 * adb logcat -s OtpHelper:*
 * ```
 *
 * The originating component is added as a `[scope]` prefix in the message instead of being the tag,
 * so the single-tag filter keeps working while still telling you where the log came from.
 *
 * Logging is intentionally always on (even in release) so production issues can be diagnosed on a
 * user's device. The volume is low because we only log at entry/setup points, not in hot loops.
 */
object AppLogger {
  const val TAG = "OtpHelper"

  fun d(scope: String, message: String) {
    Log.d(TAG, "[$scope] $message")
  }

  fun i(scope: String, message: String) {
    Log.i(TAG, "[$scope] $message")
  }

  fun w(scope: String, message: String) {
    Log.w(TAG, "[$scope] $message")
  }

  fun e(scope: String, message: String, throwable: Throwable? = null) {
    if (throwable != null) {
      Log.e(TAG, "[$scope] $message", throwable)
    } else {
      Log.e(TAG, "[$scope] $message")
    }
  }
}
