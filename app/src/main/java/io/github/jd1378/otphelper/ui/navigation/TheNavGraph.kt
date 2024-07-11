package io.github.jd1378.otphelper.ui.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import io.github.jd1378.otphelper.OTPHELPER_APP_SCHEME
import io.github.jd1378.otphelper.ui.screens.About
import io.github.jd1378.otphelper.ui.screens.History
import io.github.jd1378.otphelper.ui.screens.HistoryDetail
import io.github.jd1378.otphelper.ui.screens.HistoryDetailViewModel
import io.github.jd1378.otphelper.ui.screens.HistoryViewModel
import io.github.jd1378.otphelper.ui.screens.Home
import io.github.jd1378.otphelper.ui.screens.HomeViewModel
import io.github.jd1378.otphelper.ui.screens.IgnoredAppDetail
import io.github.jd1378.otphelper.ui.screens.IgnoredAppDetailViewModel
import io.github.jd1378.otphelper.ui.screens.IgnoredAppList
import io.github.jd1378.otphelper.ui.screens.IgnoredAppListViewModel
import io.github.jd1378.otphelper.ui.screens.IgnoredPhrases
import io.github.jd1378.otphelper.ui.screens.IgnoredPhrasesViewModel
import io.github.jd1378.otphelper.ui.screens.LanguageSelection
import io.github.jd1378.otphelper.ui.screens.LanguageSelectionViewModel
import io.github.jd1378.otphelper.ui.screens.Permissions
import io.github.jd1378.otphelper.ui.screens.PermissionsViewModel
import io.github.jd1378.otphelper.ui.screens.SensitivePhrases
import io.github.jd1378.otphelper.ui.screens.SensitivePhrasesViewModel
import io.github.jd1378.otphelper.ui.screens.Settings
import io.github.jd1378.otphelper.ui.screens.SettingsViewModel

fun NavGraphBuilder.otphelperNavGraph(
    upPress: () -> Unit,
    onNavigateToRoute: (String, Boolean) -> Unit
) {
  addHomeGraph(onNavigateToRoute)
  addLanguageSelectionGraph(upPress)
  addIgnoredAppListGraph(onNavigateToRoute, upPress)
  addIgnoredAppDetailGraph(upPress = upPress)
  addPermissionsGraph(onNavigateToRoute, upPress)
  addAboutGraph(upPress)
  addSettingsGraph(onNavigateToRoute, upPress)
  addHistoryGraph(onNavigateToRoute = onNavigateToRoute, upPress = upPress)
  addHistoryDetailGraph(upPress = upPress)
  addSensitivePhrasesGraph(upPress = upPress)
  addIgnoredPhrasesGraph(upPress = upPress)
}

fun NavGraphBuilder.addHomeGraph(
    onNavigateToRoute: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
  composable(
      MainDestinations.HOME_ROUTE,
      deepLinks =
          listOf(
              navDeepLink {
                uriPattern = "$OTPHELPER_APP_SCHEME://${MainDestinations.HOME_ROUTE}"
              }),
  ) {
    val viewModel = hiltViewModel<HomeViewModel>()
    Home(onNavigateToRoute, modifier, viewModel)
  }
}

fun NavGraphBuilder.addLanguageSelectionGraph(upPress: () -> Unit) {
  composable(
      MainDestinations.LANGUAGE_SELECTION_ROUTE,
  ) {
    val viewModel = hiltViewModel<LanguageSelectionViewModel>()
    LanguageSelection(upPress, viewModel)
  }
}

fun NavGraphBuilder.addIgnoredAppListGraph(
    onNavigateToRoute: (String, Boolean) -> Unit,
    upPress: () -> Unit
) {
  composable(
      MainDestinations.IGNORED_APP_LIST_ROUTE,
  ) {
    val viewModel = hiltViewModel<IgnoredAppListViewModel>()
    IgnoredAppList(onNavigateToRoute, upPress, viewModel)
  }
}

fun NavGraphBuilder.addIgnoredAppDetailGraph(modifier: Modifier = Modifier, upPress: () -> Unit) {
  composable(
      "${MainDestinations.IGNORED_APP_DETAIL_ROUTE}/{${NavArgs.PACKAGE_NAME}}",
      arguments = listOf(navArgument(NavArgs.PACKAGE_NAME) { type = NavType.StringType })) {
          backStackEntry ->
        val historyId = backStackEntry.arguments?.getString(NavArgs.PACKAGE_NAME)
        if (historyId.isNullOrEmpty()) {
          upPress()
        } else {
          val viewModel = hiltViewModel<IgnoredAppDetailViewModel>()
          IgnoredAppDetail(modifier, upPress, viewModel)
        }
      }
}

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

fun NavGraphBuilder.addAboutGraph(upPress: () -> Unit) {
  composable(
      MainDestinations.ABOUT_ROUTE,
  ) {
    About(upPress)
  }
}

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

fun NavGraphBuilder.addHistoryGraph(
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String, Boolean) -> Unit,
    upPress: () -> Unit
) {
  composable(MainDestinations.HISTORY_ROUTE) {
    val viewModel = hiltViewModel<HistoryViewModel>()
    History(modifier, onNavigateToRoute, upPress, viewModel)
  }
}

fun NavGraphBuilder.addHistoryDetailGraph(modifier: Modifier = Modifier, upPress: () -> Unit) {
  composable(
      "${MainDestinations.HISTORY_DETAIL_ROUTE}/{${NavArgs.HISTORY_ID}}",
      deepLinks =
          listOf(
              navDeepLink {
                uriPattern =
                    "$OTPHELPER_APP_SCHEME://${MainDestinations.HISTORY_DETAIL_ROUTE}/{${NavArgs.HISTORY_ID}}"
              }),
      arguments = listOf(navArgument(NavArgs.HISTORY_ID) { type = NavType.LongType })) {
          backStackEntry ->
        val historyId = backStackEntry.arguments?.getLong(NavArgs.HISTORY_ID)
        if (historyId == 0L) {
          upPress()
        } else {
          val viewModel = hiltViewModel<HistoryDetailViewModel>()
          HistoryDetail(modifier, upPress, viewModel)
        }
      }
}

fun NavGraphBuilder.addSensitivePhrasesGraph(modifier: Modifier = Modifier, upPress: () -> Unit) {
  composable(MainDestinations.SENSITIVE_PHRASES_ROUTE) {
    val viewModel = hiltViewModel<SensitivePhrasesViewModel>()
    SensitivePhrases(modifier, upPress, viewModel)
  }
}

fun NavGraphBuilder.addIgnoredPhrasesGraph(modifier: Modifier = Modifier, upPress: () -> Unit) {
  composable(MainDestinations.IGNORED_PHRASES_ROUTE) {
    val viewModel = hiltViewModel<IgnoredPhrasesViewModel>()
    IgnoredPhrases(modifier, upPress, viewModel)
  }
}
