package io.github.jd1378.otphelper.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.sp

@Composable
fun SettingHelp(
    text: String,
) {
  Text(
      text = text,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
      fontSize = 13.sp,
  )
}
