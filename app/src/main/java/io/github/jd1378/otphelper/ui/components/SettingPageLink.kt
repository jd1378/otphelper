package io.github.jd1378.otphelper.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import io.github.jd1378.otphelper.R

@Composable
fun SettingPageLink(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String? = null,
) {
  ListItem(
      modifier = Modifier.clip(MaterialTheme.shapes.large).then(modifier),
      headlineContent = { SettingLabel(title) },
      supportingContent = {
        if (!subtitle.isNullOrEmpty()) {
          Text(subtitle, color = MaterialTheme.colorScheme.primary)
        }
      },
      trailingContent = { Icon(painterResource(R.drawable.baseline_navigate_next_24), null) },
      colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
  )
}
