package io.github.jd1378.otphelper.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.repository.UserSettingsRepositoryMock
import io.github.jd1378.otphelper.ui.components.SettingHelp
import io.github.jd1378.otphelper.ui.components.SettingLabel
import io.github.jd1378.otphelper.ui.components.SettingPageLink
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import io.github.jd1378.otphelper.ui.theme.OtpHelperTheme
import io.github.jd1378.otphelper.ui.utils.getCurrentLocale

@Composable
fun Settings(
    onNavigateToRoute: (String, Boolean) -> Unit,
    upPress: () -> Unit,
    viewModel: SettingsViewModel
) {
  val context = LocalContext.current
  val userSettings by viewModel.userSettings.collectAsState()
  val scrollState = rememberScrollState()

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.settings),
        )
      }) { padding ->
        Column(
            modifier =
                Modifier.fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = dimensionResource(R.dimen.padding_page))
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_sections)),
        ) {
          SettingPageLink(
              modifier =
                  Modifier.clickable {
                    onNavigateToRoute(MainDestinations.LANGUAGE_SELECTION_ROUTE, false)
                  },
              title = stringResource(R.string.language),
              subtitle = getCurrentLocale().displayLanguage,
          )

          SettingPageLink(
              modifier =
                  Modifier.clickable {
                    onNavigateToRoute(MainDestinations.SENSITIVE_PHRASES_ROUTE, false)
                  },
              title = stringResource(R.string.sensitive_phrases),
          )

          SettingPageLink(
              modifier =
                  Modifier.clickable {
                    onNavigateToRoute(MainDestinations.IGNORED_PHRASES_ROUTE, false)
                  },
              title = stringResource(R.string.ignored_phrases),
          )

          Surface(
              color = MaterialTheme.colorScheme.surfaceContainer,
              shape = MaterialTheme.shapes.large,
          ) {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
                modifier =
                    Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_li_h),
                        vertical = dimensionResource(R.dimen.padding_li_v),
                    ),
            ) {
              Column(
                  verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
              ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  SettingLabel(stringResource(R.string.auto_copy))
                  Switch(
                      modifier = Modifier.height(32.dp),
                      checked = userSettings.isAutoCopyEnabled,
                      onCheckedChange = { viewModel.onAutoCopyToggle() })
                }

                SettingHelp(stringResource(R.string.auto_copy_help))
              }

              Column(
                  verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
              ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  SettingLabel(stringResource(R.string.send_detected_notif))
                  Switch(
                      modifier = Modifier.height(32.dp),
                      checked = userSettings.isPostNotifEnabled,
                      onCheckedChange = { viewModel.onPostNotifToggle() })
                }
                SettingHelp(stringResource(R.string.send_detected_notif_help))
              }

              Column(
                  verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
              ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  SettingLabel(stringResource(R.string.show_toast))
                  Switch(
                      modifier = Modifier.height(32.dp),
                      checked = userSettings.isCopiedToastEnabled,
                      onCheckedChange = { viewModel.onCopiedToastToggle() })
                }
                SettingHelp(
                    stringResource(
                        R.string.show_toast_help,
                        stringResource(R.string.code_copied_to_clipboard)))
              }

              Button(
                  modifier = Modifier.fillMaxWidth(),
                  elevation =
                      ButtonDefaults.elevatedButtonElevation(
                          defaultElevation = 1.dp,
                          pressedElevation = 0.dp,
                          disabledElevation = 0.dp,
                      ),
                  onClick = { viewModel.onSendTestNotifPressed(context) },
                  colors =
                      ButtonDefaults.buttonColors(
                          containerColor = MaterialTheme.colorScheme.secondaryContainer,
                          contentColor = MaterialTheme.colorScheme.onSecondaryContainer),
              ) {
                Text(
                    stringResource(R.string.send_test_notification), fontWeight = FontWeight.Medium)
              }
            }
          }

          Surface(
              color = MaterialTheme.colorScheme.surfaceContainer,
              shape = MaterialTheme.shapes.large,
          ) {
            Column(
                verticalArrangement =
                    Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
                modifier =
                    Modifier.padding(
                        horizontal = dimensionResource(R.dimen.padding_li_h),
                        vertical = dimensionResource(R.dimen.padding_li_v),
                    ),
            ) {
              Column(
                  verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
              ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  SettingLabel(stringResource(R.string.history))
                  Switch(
                      modifier = Modifier.height(32.dp),
                      checked = !userSettings.isHistoryDisabled,
                      onCheckedChange = { viewModel.onHistoryToggle(context) })
                }
                SettingHelp(stringResource(R.string.history_help))
              }

              Column(
                  verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
              ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  SettingLabel(stringResource(R.string.replace_code_in_history))
                  Switch(
                      enabled = !userSettings.isHistoryDisabled,
                      modifier = Modifier.height(32.dp),
                      checked = userSettings.shouldReplaceCodeInHistory,
                      onCheckedChange = { viewModel.onShouldReplaceCodeInHistoryToggle() })
                }

                SettingHelp(stringResource(R.string.replace_code_in_history_help))
              }
            }
          }

          Surface(
              color = MaterialTheme.colorScheme.surfaceContainer,
              shape = MaterialTheme.shapes.large,
              onClick = { onNavigateToRoute(MainDestinations.ABOUT_ROUTE, false) },
          ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
              Row(
                  modifier =
                      Modifier.padding(
                          horizontal = dimensionResource(R.dimen.padding_settings),
                          vertical = dimensionResource(R.dimen.padding_medium),
                      ),
                  horizontalArrangement =
                      Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
                  verticalAlignment = Alignment.CenterVertically,
              ) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = stringResource(R.string.about))
                Text(stringResource(R.string.about))
              }
            }
          }
        }
      }
}

@Preview
@Composable
fun SettingsPreview() {
  OtpHelperTheme {
    Settings(
        onNavigateToRoute = { _, _ -> {} },
        upPress = {},
        viewModel = SettingsViewModel(SavedStateHandle(), UserSettingsRepositoryMock()),
    )
  }
}
