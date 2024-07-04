package io.github.jd1378.otphelper.ui.utils

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope

@Composable
fun SkipFirstLaunchedEffect(key1: Any?, block: suspend CoroutineScope.() -> Unit) {
  var isFirstRun by remember { mutableStateOf(true) }

  LaunchedEffect(key1) {
    if (isFirstRun) {
      isFirstRun = false
      return@LaunchedEffect
    }
    block()
  }
}
