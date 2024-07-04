package io.github.jd1378.otphelper.ui.screens.permissions

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

fun NavGraphBuilder.addPermissionsGraph(
    onNavigateToRoute: (String, Boolean) -> Unit,
    upPress: () -> Unit,
) {
  composable(
      MainDestinations.PERMISSIONS_SETUP_ROUTE,
      arguments = listOf(navArgument("setup") { nullable = true })) { backStackEntry ->
        val viewModel = hiltViewModel<PermissionsViewModel>()
        Permissions(
            onNavigateToRoute,
            upPress,
            viewModel,
            backStackEntry.arguments?.containsKey("setup") ?: false)
      }
}

@Composable
fun Permissions(
    onNavigateToRoute: (String, Boolean) -> Unit,
    upPress: () -> Unit,
    viewModel: PermissionsViewModel,
    setupMode: Boolean = false
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()
  val lifecycleOwner = LocalLifecycleOwner.current
  val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsStateWithLifecycle()
  val context = LocalContext.current
  val permLauncher =
      rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) {
        viewModel.updatePermissionsStatus(context)
      }

  LaunchedEffect(lifecycleState) {
    when (lifecycleState) {
      Lifecycle.State.STARTED,
      Lifecycle.State.RESUMED -> {
        viewModel.updatePermissionsStatus(context)
      }
      else -> {}
    }
  }
  LaunchedEffect(uiState.userSettings.isSetupFinished) {
    if (uiState.userSettings.isSetupFinished) {
      onNavigateToRoute(MainDestinations.HOME_ROUTE, true)
    }
  }

  Scaffold(
      topBar = {
        TitleBar(
            upPress = upPress,
            text = LocalContext.current.getString(R.string.PERMISSION_ROUTE),
            showBackBtn = !setupMode) {
              if (setupMode) {
                Button(
                    modifier =
                        Modifier.padding(
                            start = 40.dp, end = dimensionResource(R.dimen.padding_small)),
                    onClick = {
                      onNavigateToRoute(MainDestinations.LANGUAGE_SELECTION_ROUTE, false)
                    }) {
                      Text(text = stringResource(R.string.language))
                    }
              }
            }
      },
      bottomBar = {
        if (setupMode) {
          SkipDialog(show = uiState.showSkipWarning) { viewModel.onSetupFinish() }
          Row(Modifier.navigationBarsPadding().padding(10.dp)) {
            Spacer(modifier = Modifier.weight(1f))
            if (uiState.hasDoneAllSteps) {
              Button(onClick = { viewModel.onSetupFinish() }) {
                Text(text = stringResource(R.string.finish))
              }
            } else {
              OutlinedButton(onClick = { viewModel.onSetupSkipPressed() }) {
                Text(text = stringResource(R.string.skip))
              }
            }
          }
        }
      },
  ) { padding ->
    Column(
        Modifier.padding(padding)
            .padding(top = 10.dp)
            .padding(horizontal = dimensionResource(R.dimen.padding_small))
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
      Text(
          stringResource(R.string.permissions_desc),
          modifier =
              Modifier.fillMaxWidth()
                  .padding(horizontal = dimensionResource(R.dimen.padding_small)),
          fontSize = 15.sp)

      TodoItem(
          stringResource(R.string.permission_todo_post_notifications),
          actionText = stringResource(R.string.grant),
          enabled = !uiState.hasNotifPerm,
          checked = uiState.hasNotifPerm) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
              permLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
          }
      TodoItem(
          stringResource(R.string.permission_todo_read_notifications),
          checked = uiState.hasNotifListenerPerm) {
            viewModel.onOpenReadNotificationsPressed(context)
          }
      TodoItem(
          stringResource(R.string.permission_todo_remain_open),
          checked = uiState.isIgnoringBatteryOptimizations) {
            viewModel.onOpenBatteryOptimizationsPressed(context)
          }

      if (uiState.hasAutostartSettings) {
        Text(
            stringResource(R.string.perm_extra_desc),
            modifier =
                Modifier.fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_small)),
            fontSize = 15.sp)

        TodoItem(
            stringResource(R.string.permission_todo_autostart),
            intermediate = true,
            checked = true) {
              viewModel.onOpenAutostartPressed(context)
            }
      }

      if (uiState.hasRestrictedSettings) {
        Text(
            stringResource(R.string.perm_restricted_desc),
            modifier =
                Modifier.fillMaxWidth()
                    .padding(horizontal = dimensionResource(R.dimen.padding_small)),
            fontSize = 15.sp)

        TodoItem(
            stringResource(R.string.permission_todo_allow_restricted_settings),
            intermediate = true,
            checked = true) {
              viewModel.onOpenAppSettings(context)
            }
      }
    }
  }
}
