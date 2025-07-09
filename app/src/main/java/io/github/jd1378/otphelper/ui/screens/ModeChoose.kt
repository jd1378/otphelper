package io.github.jd1378.otphelper.ui.screens

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jd1378.otphelper.BuildConfig
import io.github.jd1378.otphelper.ModeOfOperation
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

@Composable
fun ModeChoose(
    onNavigateToRoute: ((String, Boolean, Boolean) -> Unit),
    upPress: () -> Unit,
    viewModel: ModeChooseViewModel,
    setupMode: Boolean = false,
) {
  val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
  val smsEnabled = remember { BuildConfig.SMS_MODE_AVAILABLE }
  val context = LocalContext.current

  Scaffold(
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.MODE_ROUTE),
            showBackBtn = !setupMode) {
              if (setupMode) {
                Button(
                    modifier =
                        Modifier.padding(
                            start = 40.dp, end = dimensionResource(R.dimen.padding_small)),
                    onClick = {
                      onNavigateToRoute(MainDestinations.LANGUAGE_SELECTION_ROUTE, false, true)
                    }) {
                      Text(text = stringResource(R.string.language))
                    }
              }
            }
      },
      bottomBar = {
        if (setupMode) {
          Row(Modifier.navigationBarsPadding().padding(10.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            Button(
                onClick = {
                  onNavigateToRoute(MainDestinations.PERMISSIONS_SETUP_ROUTE, false, false)
                },
                enabled = userSettings.modeOfOperation != ModeOfOperation.UNRECOGNIZED) {
                  Text(text = stringResource(R.string.next))
                }
          }
        }
      },
  ) { padding ->
    Column(
        Modifier.padding(padding)
            .padding(top = 10.dp)
            .padding(horizontal = dimensionResource(R.dimen.padding_page))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_page)),
    ) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
        Text(
            text = stringResource(R.string.mode_choose_help),
            style =
                TextStyle(
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 0.3.sp),
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)))
      }
      Surface(
          color = MaterialTheme.colorScheme.surfaceContainer,
          shape = MaterialTheme.shapes.large,
          onClick = { viewModel.onModeSelected(context, ModeOfOperation.Notification) }) {
            Row {
              RadioButton(
                  selected = userSettings.modeOfOperation == ModeOfOperation.Notification,
                  onClick = { viewModel.onModeSelected(context, ModeOfOperation.Notification) })
              Column(Modifier.padding(vertical = dimensionResource(R.dimen.padding_xs))) {
                Text(
                    text = stringResource(R.string.Notification),
                    fontSize = 20.sp,
                )
                Text(
                    text =
                        stringResource(
                            R.string.notification_mode_help,
                        ),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Justify,
                )
              }
            }
          }

      Surface(
          color =
              if (smsEnabled) MaterialTheme.colorScheme.surfaceContainer
              else MaterialTheme.colorScheme.surfaceContainerLow,
          shape = MaterialTheme.shapes.large,
          onClick = { if (smsEnabled) viewModel.onModeSelected(context, ModeOfOperation.SMS) },
          enabled = smsEnabled,
      ) {
        Row {
          RadioButton(
              selected = userSettings.modeOfOperation == ModeOfOperation.SMS,
              onClick = { if (smsEnabled) viewModel.onModeSelected(context, ModeOfOperation.SMS) },
              enabled = smsEnabled,
          )
          Column(Modifier.padding(vertical = dimensionResource(R.dimen.padding_xs))) {
            Text(
                text = stringResource(R.string.sms),
                style = MaterialTheme.typography.titleMedium,
                color =
                    if (smsEnabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
            Text(
                text = stringResource(R.string.sms_mode_help),
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Justify,
                color =
                    if (smsEnabled) MaterialTheme.colorScheme.onSurface
                    else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f))
          }
        }
      }

      if (!smsEnabled) {
        Text(
            text = stringResource(R.string.mode_sms_disabled_help),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(dimensionResource(R.dimen.padding_small)),
            color = MaterialTheme.colorScheme.onBackground,
        )
      }
    }
  }
}
