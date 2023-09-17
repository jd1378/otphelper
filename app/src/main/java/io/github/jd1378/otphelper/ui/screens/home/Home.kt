package io.github.jd1378.otphelper.ui.screens.home

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.LocaleDropdownMenu
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

fun NavGraphBuilder.addHomeGraph(
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
  composable(MainDestinations.HOME_ROUTE) {
    val viewModel = hiltViewModel<HomeViewModel>()
    Home(onNavigateToRoute, modifier, viewModel)
  }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Home(onNavigateToRoute: (String) -> Unit, modifier: Modifier, viewModel: HomeViewModel) {
  val uiState by viewModel.uiState.collectAsState()
  val context = LocalContext.current

  LaunchedEffect(uiState.isSetupFinished) {
    if (!uiState.isSetupFinished) {
      onNavigateToRoute(MainDestinations.PERMISSIONS_ROUTE + "?setup=true")
    }
  }

  Scaffold(modifier = modifier) { padding ->
    Column(
        modifier = Modifier.fillMaxSize().padding(padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
      Column(
          Modifier.width(IntrinsicSize.Max),
          verticalArrangement = Arrangement.spacedBy(8.dp),
      ) {
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigateToRoute(MainDestinations.PERMISSIONS_ROUTE) }) {
              Text(text = stringResource(R.string.PERMISSION_ROUTE))
            }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { viewModel.onSendTestNotifPressed(context) }) {
              Text(text = stringResource(R.string.send_test_notification))
            }
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onNavigateToRoute(MainDestinations.IGNORED_LIST_ROUTE) }) {
              Text(text = stringResource(R.string.ignored_list))
            }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Text(text = stringResource(R.string.auto_copy), Modifier.padding(horizontal = 10.dp))
          Switch(
              checked = uiState.isAutoCopyEnabled,
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
              checked = uiState.isPostNotifEnabled,
              onCheckedChange = { viewModel.onPostNotifToggle() })
        }
        LocaleDropdownMenu()
      }
    }
  }
}
