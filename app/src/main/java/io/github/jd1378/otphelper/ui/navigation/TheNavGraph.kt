package io.github.jd1378.otphelper.ui.navigation

import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import io.github.jd1378.otphelper.OTPHELPER_APP_SCHEME
import io.github.jd1378.otphelper.ui.screens.about.About
import io.github.jd1378.otphelper.ui.screens.history.History
import io.github.jd1378.otphelper.ui.screens.history.HistoryViewModel
import io.github.jd1378.otphelper.ui.screens.historydetail.HistoryDetail
import io.github.jd1378.otphelper.ui.screens.historydetail.HistoryDetailViewModel
import io.github.jd1378.otphelper.ui.screens.home.Home
import io.github.jd1378.otphelper.ui.screens.home.HomeViewModel
import io.github.jd1378.otphelper.ui.screens.ignored_app_list.IgnoredAppList
import io.github.jd1378.otphelper.ui.screens.ignored_app_list.IgnoredAppListViewModel
import io.github.jd1378.otphelper.ui.screens.language_selection.LanguageSelection
import io.github.jd1378.otphelper.ui.screens.language_selection.LanguageSelectionViewModel
import io.github.jd1378.otphelper.ui.screens.permissions.Permissions
import io.github.jd1378.otphelper.ui.screens.permissions.PermissionsViewModel
import io.github.jd1378.otphelper.ui.screens.settings.addSettingsGraph

fun NavGraphBuilder.otphelperNavGraph(
    upPress: () -> Unit,
    onNavigateToRoute: (String, Boolean) -> Unit
) {
  addHomeGraph(onNavigateToRoute)
  addLanguageSelectionGraph(upPress)
  addIgnoredAppListGraph(upPress)
  addPermissionsGraph(onNavigateToRoute, upPress)
  addAboutGraph(upPress)
  addSettingsGraph(onNavigateToRoute, upPress)
  addHistoryGraph(onNavigateToRoute = onNavigateToRoute, upPress = upPress)
  addHistoryDetailGraph(upPress = upPress)
}

fun NavGraphBuilder.addHomeGraph(
    onNavigateToRoute: (String, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
  composable(MainDestinations.HOME_ROUTE) {
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

fun NavGraphBuilder.addIgnoredAppListGraph(upPress: () -> Unit) {
  composable(
      MainDestinations.IGNORED_LIST_ROUTE,
  ) {
    val viewModel = hiltViewModel<IgnoredAppListViewModel>()
    IgnoredAppList(upPress, viewModel)
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
