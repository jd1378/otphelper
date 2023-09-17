package io.github.jd1378.otphelper.ui.components

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import io.github.jd1378.otphelper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocaleDropdownMenu(modifier: Modifier = Modifier) {
  val localeOptions =
      mapOf(R.string.system_default to "default", R.string.en to "en", R.string.fa to "fa")
          .mapKeys { stringResource(it.key) }

  var expanded by remember { mutableStateOf(false) }
  ExposedDropdownMenuBox(
      modifier = modifier, expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        CustomTextField(
            modifier = Modifier.menuAnchor().clip(RoundedCornerShape(5.dp)).height(40.dp),
            readOnly = true,
            value = stringResource(R.string.language),
            onValueChange = {},
            singleLine = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
          localeOptions.keys.forEach { selectionLocale ->
            DropdownMenuItem(
                text = { Text(selectionLocale) },
                onClick = {
                  expanded = false

                  if (localeOptions[selectionLocale] == "default") {
                    AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
                  } else {
                    AppCompatDelegate.setApplicationLocales(
                        LocaleListCompat.forLanguageTags(localeOptions[selectionLocale]),
                    )
                  }
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            )
          }
        }
      }
}
