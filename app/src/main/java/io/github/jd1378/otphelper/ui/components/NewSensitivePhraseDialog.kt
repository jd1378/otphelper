package io.github.jd1378.otphelper.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.theme.OtpHelperTheme
import io.github.jd1378.otphelper.utils.CodeExtractor

fun isPhraseParsable(str: String): Boolean {
  if (str.isBlank()) return false
  return try {
    CodeExtractor(listOf(str, "code")).getCode("Code: 123456") == "123456"
  } catch (e: Throwable) {
    false
  }
}

@Composable
fun NewSensitivePhraseDialog(
    onDismissRequest: () -> Unit = {},
    onConfirm: (String) -> Unit = {},
) {
  var newPhrase by remember { mutableStateOf("") }

  val confirmEnabled = remember { derivedStateOf { isPhraseParsable(newPhrase) } }

  Dialog(
      onDismissRequest = { onDismissRequest() },
  ) {
    Surface(shape = MaterialTheme.shapes.large, tonalElevation = 6.dp) {
      Column(
          Modifier.padding(dimensionResource(R.dimen.padding_large)),
          verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_large)),
      ) {
        Text(text = stringResource(R.string.new_sensitive_phrase), fontSize = 20.sp)
        OutlinedTextField(
            modifier = Modifier.heightIn(min = 120.dp),
            value = newPhrase,
            onValueChange = { newPhrase = it },
            placeholder = { Text(stringResource(R.string.your_new_phrase)) },
        )

        Row(
            Modifier.fillMaxWidth(1f),
            horizontalArrangement = Arrangement.End,
        ) {
          TextButton(
              onClick = { onDismissRequest() },
              colors =
                  ButtonDefaults.textButtonColors()
                      .copy(contentColor = MaterialTheme.colorScheme.error),
          ) {
            Text(stringResource(R.string.cancel))
          }

          TextButton(
              enabled = confirmEnabled.value,
              onClick = { onConfirm(newPhrase) },
              colors =
                  ButtonDefaults.textButtonColors()
                      .copy(contentColor = MaterialTheme.colorScheme.primary),
          ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
              Icon(Icons.Default.Add, null)
              Text(stringResource(R.string.add))
            }
          }
        }
      }
    }
  }
}

@Preview(showBackground = true, widthDp = 300, locale = "en")
@Composable
fun NewSensitivePhraseDialogPreview() {
  OtpHelperTheme { NewSensitivePhraseDialog() {} }
}
