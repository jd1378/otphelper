package io.github.jd1378.otphelper.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

fun NavGraphBuilder.addSettingsGraph(upPress: () -> Unit) {
  composable(
      MainDestinations.SETTINGS_ROUTE,
  ) {
    val viewModel = hiltViewModel<SettingsViewModel>()
    Settings(upPress, viewModel)
  }
}

@Composable
fun Settings(upPress: () -> Unit, viewModel: SettingsViewModel) {

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
            modifier = Modifier.fillMaxSize().padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
          Column(
              Modifier.width(IntrinsicSize.Max),
              verticalArrangement = Arrangement.spacedBy(8.dp),
          ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(text = stringResource(R.string.auto_copy), Modifier.padding(horizontal = 10.dp))
              Switch(
                  checked = userSettings.isAutoCopyEnabled,
                  onCheckedChange = { viewModel.onAutoCopyToggle() })
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(
                  text = stringResource(R.string.send_detected_notif),
                  Modifier.padding(horizontal = 10.dp))
              Switch(
                  checked = userSettings.isPostNotifEnabled,
                  onCheckedChange = { viewModel.onPostNotifToggle() })
            }
          }
        }
      }
}
