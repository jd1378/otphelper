package io.github.jd1378.otphelper.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight

@Composable
fun RowScope.SettingLabel(
    text: String,
) {
  SettingLabel(text = text, modifier = Modifier.weight(1f))
}

@Composable
fun SettingLabel(
    text: String,
    modifier: Modifier = Modifier,
) {
  Text(text = text, fontWeight = FontWeight.Medium, modifier = modifier)
}
