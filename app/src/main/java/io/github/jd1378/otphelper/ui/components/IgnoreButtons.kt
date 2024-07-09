package io.github.jd1378.otphelper.ui.components

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import io.github.jd1378.otphelper.R

@Composable
fun IgnoreToggleButton(
    isIgnored: Boolean,
    ignoreText: String,
    allowText: String,
    onClick: () -> Unit
) {
  OutlinedButton(
      onClick = { onClick() },
      colors =
          if (isIgnored) ButtonDefaults.outlinedButtonColors()
          else
              ButtonDefaults.outlinedButtonColors()
                  .copy(
                      contentColor = MaterialTheme.colorScheme.error,
                  ),
  ) {
    if (isIgnored) {
      Text(allowText)
    } else {
      Text(ignoreText)
    }
  }
}

@Composable
fun IgnoreAppButton(isIgnored: Boolean, onClick: () -> Unit) {
  IgnoreToggleButton(
      isIgnored, stringResource(R.string.ignore_app), stringResource(R.string.allow_app)) {
        onClick()
      }
}

@Composable
fun IgnoreNotifIdButton(isIgnored: Boolean, onClick: () -> Unit) {
  IgnoreToggleButton(
      isIgnored, stringResource(R.string.ignore_id), stringResource(R.string.allow_id)) {
        onClick()
      }
}

@Composable
fun IgnoreNotifTagButton(isIgnored: Boolean, onClick: () -> Unit) {
  IgnoreToggleButton(
      isIgnored, stringResource(R.string.ignore_tag), stringResource(R.string.allow_tag)) {
        onClick()
      }
}
