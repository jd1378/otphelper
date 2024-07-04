package io.github.jd1378.otphelper.ui.screens.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.SettingPageLink
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import java.util.Locale

fun NavGraphBuilder.addSettingsGraph(
    onNavigateToRoute: (String, Boolean) -> Unit,
    upPress: () -> Unit
) {
  composable(
      MainDestinations.SETTINGS_ROUTE,
  ) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    Settings(onNavigateToRoute, upPress, viewModel)
  }
}

@Composable
fun Settings(
    onNavigateToRoute: (String, Boolean) -> Unit,
    upPress: () -> Unit,
    viewModel: SettingsViewModel
) {

  val userSettings by viewModel.userSettings.collectAsState()

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
                    .padding(horizontal = dimensionResource(R.dimen.padding_page)),
        ) {
          Column(
              verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            SettingPageLink(
                modifier =
                    Modifier.clickable {
                      onNavigateToRoute(MainDestinations.LANGUAGE_SELECTION_ROUTE, false)
                    },
                title = stringResource(R.string.language),
                subtitle = getCurrentLocale().displayLanguage,
            )
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = MaterialTheme.shapes.large,
            ) {
              Column(
                  verticalArrangement = Arrangement.spacedBy(8.dp),
                  modifier =
                      Modifier.padding(
                          horizontal = dimensionResource(R.dimen.padding_li_h),
                          vertical = dimensionResource(R.dimen.padding_li_v),
                      ),
              ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text(text = stringResource(R.string.auto_copy))
                  Switch(
                      checked = userSettings.isAutoCopyEnabled,
                      onCheckedChange = { viewModel.onAutoCopyToggle() })
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                  Text(text = stringResource(R.string.send_detected_notif))
                  Switch(
                      checked = userSettings.isPostNotifEnabled,
                      onCheckedChange = { viewModel.onPostNotifToggle() })
                }
              }
            }
          }
        }
      }
}

@Composable
fun getCurrentLocale(): Locale {
  val context = LocalContext.current
  return context.resources.configuration.locales[0]
}
