package io.github.jd1378.otphelper.model

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

interface Translatable {
  @get:StringRes val translation: Int?
}

@Composable
fun Translatable.getTranslation(fallback: String? = null): String {
  return this.translation?.let { stringResource(it) } ?: fallback ?: this.toString()
}

@Composable
fun Translatable.getTranslation(@StringRes fallback: Int): String {
  return this.getTranslation(stringResource(fallback))
}
