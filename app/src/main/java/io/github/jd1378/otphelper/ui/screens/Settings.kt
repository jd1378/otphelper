package io.github.jd1378.otphelper.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.SavedStateHandle
import io.github.jd1378.otphelper.ModeOfOperation
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.repository.UserSettingsRepositoryMock
import io.github.jd1378.otphelper.ui.components.CodeBlock
import io.github.jd1378.otphelper.ui.components.SettingHelp
import io.github.jd1378.otphelper.ui.components.SettingLabel
import io.github.jd1378.otphelper.ui.components.SettingPageLink
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import io.github.jd1378.otphelper.ui.theme.OtpHelperTheme
import io.github.jd1378.otphelper.ui.utils.getCurrentLocale
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun Settings(
    onNavigateToRoute: (String, Boolean, Boolean) -> Unit,
    upPress: () -> Unit,
    viewModel: SettingsViewModel,
) {
  val scope = rememberCoroutineScope()
  val context = LocalContext.current
  val userSettings by viewModel.userSettings.collectAsState()
  val scrollState = rememberScrollState()
  val focusManager = LocalFocusManager.current
  var isInitialLoad by remember { mutableStateOf(true) }
  var targetPackageName by remember { mutableStateOf("") }

  LaunchedEffect(Unit) {
    scope.launch {
      targetPackageName = viewModel.getBroadcastTargetPackageName()
      isInitialLoad = false
    }
  }

  LaunchedEffect(targetPackageName) {
    if (isInitialLoad) return@LaunchedEffect
    // effectively debounce
    delay(100)
    viewModel.setBroadcastTargetPackageName(targetPackageName)
  }

  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = { TitleBar(upPress = upPress, text = stringResource(R.string.settings)) },
  ) { padding ->
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
                onNavigateToRoute(MainDestinations.LANGUAGE_SELECTION_ROUTE, false, true)
              },
          title = stringResource(R.string.language),
          subtitle = getCurrentLocale()?.displayLanguage ?: stringResource(R.string.error),
      )

      SettingPageLink(
          modifier =
              Modifier.clickable {
                onNavigateToRoute(MainDestinations.SENSITIVE_PHRASES_ROUTE, false, true)
              },
          title = stringResource(R.string.sensitive_phrases),
      )

      SettingPageLink(
          modifier =
              Modifier.clickable {
                onNavigateToRoute(MainDestinations.IGNORED_PHRASES_ROUTE, false, true)
              },
          title = stringResource(R.string.ignored_phrases),
      )

      SettingPageLink(
          modifier =
              Modifier.clickable {
                onNavigateToRoute(MainDestinations.CLEANUP_PHRASES_ROUTE, false, true)
              },
          title = stringResource(R.string.cleanup_phrases),
      )

      SettingPageLink(
          modifier =
              Modifier.clickable {
                onNavigateToRoute(MainDestinations.DETECTION_TEST_ROUTE, false, true)
              },
          title = stringResource(R.string.detection_test),
      )

      Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = MaterialTheme.shapes.large,
      ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
            modifier =
                Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_li_h),
                    vertical = dimensionResource(R.dimen.padding_li_v),
                ),
        ) {
          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
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
                  onCheckedChange = { viewModel.onAutoCopyToggle() },
              )
            }

            SettingHelp(stringResource(R.string.auto_copy_help))
          }

          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
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
                  onCheckedChange = { viewModel.onPostNotifToggle() },
              )
            }
            SettingHelp(stringResource(R.string.send_detected_notif_help))
          }

          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
              verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
          ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              SettingLabel(stringResource(R.string.show_copy_confirmation))
              Switch(
                  modifier = Modifier.height(32.dp),
                  checked = userSettings.isShowCopyConfirmationEnabled,
                  onCheckedChange = { viewModel.onShowCopyConfirmationToggle() },
              )
            }
            SettingHelp(
                stringResource(
                    R.string.show_confirmation_help,
                    stringResource(R.string.code_copied_to_clipboard),
                )
            )
          }

          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
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
                  checked = userSettings.isShowToastEnabled,
                  onCheckedChange = { viewModel.onShowToastToggle() },
              )
            }
            SettingHelp(stringResource(R.string.show_toast_help))
          }

          if (userSettings.modeOfOperation == ModeOfOperation.Notification) {
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
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
            ) {
              Text(stringResource(R.string.send_test_notification), fontWeight = FontWeight.Medium)
            }
          }
        }
      }

      Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = MaterialTheme.shapes.large,
      ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
            modifier =
                Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_li_h),
                    vertical = dimensionResource(R.dimen.padding_li_v),
                ),
        ) {
          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
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
                  onCheckedChange = { viewModel.onHistoryToggle(context) },
              )
            }
            SettingHelp(stringResource(R.string.history_help))
          }

          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
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
                  onCheckedChange = { viewModel.onShouldReplaceCodeInHistoryToggle() },
              )
            }

            SettingHelp(stringResource(R.string.replace_code_in_history_help))
          }
        }
      }

      Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = MaterialTheme.shapes.large,
      ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
            modifier =
                Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_li_h),
                    vertical = dimensionResource(R.dimen.padding_li_v),
                ),
        ) {
          SettingHelp(stringResource(R.string.experimental_features_warning))
          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
              verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
          ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              SettingLabel(stringResource(R.string.auto_dismiss))
              Switch(
                  modifier = Modifier.height(32.dp),
                  checked = userSettings.isAutoDismissEnabled,
                  onCheckedChange = { viewModel.onAutoDismissToggle() },
              )
            }
            SettingHelp(stringResource(R.string.auto_dismiss_desc))
          }

          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
              verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
          ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              SettingLabel(stringResource(R.string.auto_mark_as_read))
              Switch(
                  modifier = Modifier.height(32.dp),
                  checked = userSettings.isAutoMarkAsReadEnabled,
                  onCheckedChange = { viewModel.onAutoMarkAsReadToggle() },
              )
            }

            SettingHelp(stringResource(R.string.auto_mark_as_read_help))
          }
        }
      }

      Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = MaterialTheme.shapes.large,
      ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
            modifier =
                Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_li_h),
                    vertical = dimensionResource(R.dimen.padding_li_v),
                ),
        ) {
          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
              verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
          ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              SettingLabel(stringResource(R.string.copy_as_not_sensitive))
              Switch(
                  modifier = Modifier.height(32.dp),
                  checked = userSettings.isCopyAsNotSensitiveEnabled,
                  onCheckedChange = { viewModel.onIsCopyAsNotSensitiveToggle() },
              )
            }
            SettingHelp(stringResource(R.string.copy_as_not_sensitive_desc))
          }
        }
      }

      Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = MaterialTheme.shapes.large,
      ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
            modifier =
                Modifier.padding(
                    horizontal = dimensionResource(R.dimen.padding_li_h),
                    vertical = dimensionResource(R.dimen.padding_li_v),
                ),
        ) {
          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
              verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
          ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              SettingLabel(stringResource(R.string.broadcast_code))
              Switch(
                  modifier = Modifier.height(32.dp),
                  checked = userSettings.isBroadcastCodeEnabled,
                  onCheckedChange = { viewModel.onBroadcastCodeToggle() },
              )
            }
            SettingHelp(stringResource(R.string.broadcast_code_help))
          }

          TextField(
              modifier = Modifier.fillMaxWidth(),
              label = { Text(stringResource(R.string.target_package_name)) },
              placeholder = { Text("e.g. com.llamalab.automate") },
              value = targetPackageName,
              onValueChange = { targetPackageName = it },
              supportingText = { SettingHelp(stringResource(R.string.broadcast_target_help)) },
              keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
              keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
          )

          Column(
              modifier = Modifier.semantics(mergeDescendants = true) {},
              verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
          ) {
            Text(stringResource(R.string.intent_action))
            CodeBlock(stringResource(R.string.intent_action_code_detected))
            SettingHelp(stringResource(R.string.intent_action_code_detected_help))
          }
        }
      }

      Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = MaterialTheme.shapes.large,
          onClick = { onNavigateToRoute(MainDestinations.ABOUT_ROUTE, false, true) },
          modifier = Modifier.semantics(mergeDescendants = true) {},
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
              horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_xs)),
              verticalAlignment = Alignment.CenterVertically,
          ) {
            Icon(
                imageVector = Icons.Outlined.Info,
                contentDescription = stringResource(R.string.about),
            )
            Text(stringResource(R.string.about))
          }
        }
      }
    }
  }
}

@SuppressLint("ViewModelConstructorInComposable")
@Preview
@Composable
fun SettingsPreview() {
  OtpHelperTheme {
    Settings(
        onNavigateToRoute = { _, _, _ -> {} },
        upPress = {},
        viewModel = SettingsViewModel(SavedStateHandle(), UserSettingsRepositoryMock()),
    )
  }
}
