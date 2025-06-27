package io.github.jd1378.otphelper.di

import androidx.compose.runtime.Stable
import javax.inject.Inject
import javax.inject.Singleton

data class RecentDetectedMessage(
    val body: String,
    val timestamp: Long,
)

@Singleton
@Stable
class RecentDetectedMessageHolder @Inject constructor() {
  var message: RecentDetectedMessage? = null
}

// sometimes SMS may take several seconds to show as notification if the system is overloaded,
// but I'm keeping it to 2s to prevent incorrect interactions as much as possible
const val DETECTION_TIMEOUT_MS = 2_000L

val DETECTION_LOCK = Any()
