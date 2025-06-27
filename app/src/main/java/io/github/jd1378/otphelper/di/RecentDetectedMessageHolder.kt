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
