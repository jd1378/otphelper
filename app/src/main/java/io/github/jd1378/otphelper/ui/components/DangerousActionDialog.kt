package io.github.jd1378.otphelper.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import io.github.jd1378.otphelper.R

@Composable
fun DangerousActionDialog(
    title: String = stringResource(R.string.notice),
    onDismissRequest: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
  AlertDialog(
      onDismissRequest = onDismissRequest,
      icon = { Icon(Icons.Outlined.Warning, null) },
      title = { Text(title) },
      text = {
        Text(
            text = stringResource(R.string.irreversible_action_warning),
            textAlign = TextAlign.Center,
        )
      },
      confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.yes)) } },
      dismissButton = {
        TextButton(onClick = onDismissRequest) { Text(stringResource(R.string.no)) }
      },
  )
}

@Preview(showBackground = true, widthDp = 300, locale = "en")
@Composable
fun DangerousActionDialogPreview() {
  DangerousActionDialog() {}
}

@Preview(showBackground = true, widthDp = 300, locale = "fa")
@Composable
fun DangerousActionDialogPreviewFa() {
  DangerousActionDialog() {}
}
