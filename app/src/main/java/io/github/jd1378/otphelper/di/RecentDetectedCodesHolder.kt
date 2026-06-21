package io.github.jd1378.otphelper.di

import androidx.compose.runtime.Stable
import javax.inject.Inject
import javax.inject.Singleton

// Window during which an identical detection (same package + text + code) is
// treated as a duplicate and not recorded again. Some apps (e.g. Gmail) repost
// the same notification with a new notification id within a few seconds, which
// previously caused the same code to be captured multiple times. See issue #217.
const val DUPLICATE_DETECTION_WINDOW_MS = 10_000L

@Singleton
@Stable
class RecentDetectedCodesHolder @Inject constructor() {
  private val recentSignatures = HashMap<String, Long>()

  // Returns true if an identical detection was already seen within the
  // deduplication window. Records (or refreshes) the signature otherwise so that
  // a train of reposts keeps extending the window instead of slipping through.
  @Synchronized
  fun isDuplicate(signature: String, now: Long): Boolean {
    pruneExpired(now)
    val lastSeen = recentSignatures[signature]
    recentSignatures[signature] = now
    return lastSeen != null && now - lastSeen <= DUPLICATE_DETECTION_WINDOW_MS
  }

  private fun pruneExpired(now: Long) {
    val iterator = recentSignatures.iterator()
    while (iterator.hasNext()) {
      if (now - iterator.next().value > DUPLICATE_DETECTION_WINDOW_MS) {
        iterator.remove()
      }
    }
  }
}
