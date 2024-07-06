package io.github.jd1378.otphelper.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

@Composable
fun SettingLabel(
    text: String,
) {
  Text(text = text, fontWeight = FontWeight.Medium)
}
