package io.github.jd1378.otphelper.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TriStateCheckbox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.SemanticsPropertyReceiver
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.theme.OtpHelperTheme

@Composable
fun TodoItem(
    text: String,
    modifier: Modifier = Modifier,
    checked: Boolean = false,
    actionText: String = stringResource(R.string.open),
    intermediate: Boolean = false,
    buttonEnabled: Boolean = true,
    checkboxSemantics: SemanticsPropertyReceiver.() -> Unit = {},
    buttonSemantics: SemanticsPropertyReceiver.() -> Unit = {},
    onActionPressed: () -> Unit,
) {
  Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxWidth().semantics(true) {},
  ) {
    TriStateCheckbox(
        modifier = Modifier.semantics(false, checkboxSemantics).padding(dimensionResource(R.dimen.padding_small)),
        state = if (intermediate) ToggleableState.Indeterminate else ToggleableState(checked),
        onClick = null,
        enabled = !intermediate,
    )
    Text(text, fontSize = 18.sp, modifier = Modifier.weight(1f).padding(end = 5.dp).then(modifier))
    if (checked) {
      OutlinedButton(
          modifier = Modifier.semantics(false, buttonSemantics),
          onClick = onActionPressed,
          enabled = buttonEnabled) {
            Text(actionText)
          }
    } else {
      Button(modifier = Modifier.semantics(false, buttonSemantics), onClick = onActionPressed) {
        Text(actionText)
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 300)
@Composable
fun TodoItemPreview() {
  OtpHelperTheme {
    Column {
      TodoItem("my todo item") {}
      TodoItem("A very long todo Item list to show line break") {}
    }
  }
}
