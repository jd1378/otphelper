package io.github.jd1378.otphelper.ui.screens.settings

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

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
