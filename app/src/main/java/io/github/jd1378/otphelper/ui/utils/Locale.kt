package io.github.jd1378.otphelper.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.util.Locale

@Composable
fun getCurrentLocale(): Locale {
  return LocalConfiguration.current.locales[0]
}
