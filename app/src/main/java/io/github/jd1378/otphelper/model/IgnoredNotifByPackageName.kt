package io.github.jd1378.otphelper.model

import androidx.compose.runtime.Immutable

@Immutable
data class IgnoredNotifsOfPackageName(
    val packageName: String,
    val totalItems: Long,
)
