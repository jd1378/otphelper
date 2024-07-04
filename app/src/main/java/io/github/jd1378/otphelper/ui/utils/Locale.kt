package io.github.jd1378.otphelper.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

@Composable
fun getCurrentLocale(): Locale {
  val context = LocalContext.current
  return context.resources.configuration.locales[0]
}
