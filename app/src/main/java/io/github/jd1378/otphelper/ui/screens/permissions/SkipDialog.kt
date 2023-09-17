package io.github.jd1378.otphelper.ui.screens.permissions

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import io.github.jd1378.otphelper.R

@Composable
fun SkipDialog(show: Boolean, onConfirm: () -> Unit) {
  if (show) {
    AlertDialog(
        onDismissRequest = {
          // disable
        },
        title = { Text(text = stringResource(R.string.notice)) },
        text = { Text(text = stringResource(R.string.skip_warning)) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(stringResource(R.string.okay)) } },
    )
  }
}

@Preview(showBackground = true, widthDp = 300, locale = "en")
@Composable
fun SkipDialogPreview() {
  SkipDialog(show = true) {
    //
  }
}

@Preview(showBackground = true, widthDp = 300, locale = "fa")
@Composable
fun SkipDialogPreviewFa() {
  SkipDialog(show = true) {
    //
  }
}
