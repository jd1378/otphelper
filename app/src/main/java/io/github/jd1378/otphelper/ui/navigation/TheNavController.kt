package io.github.jd1378.otphelper.ui.navigation

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavGraph
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

// this file is made using help from compose-samples/Jetsnack

object MainDestinations {
  const val HOME_ROUTE = "home"
  const val LANGUAGE_SELECTION_ROUTE = "language_selection"
  const val IGNORED_APP_LIST_ROUTE = "ignored_app_list"
  const val IGNORED_APP_DETAIL_ROUTE = "ignored_app_detail"
  const val PERMISSIONS_ROUTE = "permissions"
  const val PERMISSIONS_SETUP_ROUTE = "permissions?setup={setup}"
  const val MODE_ROUTE = "mode"
  const val MODE_SETUP_ROUTE = "mode?setup={setup}"
  const val ABOUT_ROUTE = "about"
  const val SETTINGS_ROUTE = "settings"
  const val HISTORY_ROUTE = "history"
  const val HISTORY_DETAIL_ROUTE = "history_detail"
  const val SENSITIVE_PHRASES_ROUTE = "sensitive_phrases"
  const val IGNORED_PHRASES_ROUTE = "ignored_phrases"
  const val CLEANUP_PHRASES_ROUTE = "cleanup_phrases"
  const val DETECTION_TEST_ROUTE = "detection_test"
}

object NavArgs {
  const val HISTORY_ID = "historyId"
  const val PACKAGE_NAME = "packageName"
}

@Composable
fun rememberTheNavController(
    navController: NavHostController = rememberNavController()
): TheNavController = remember(navController) { TheNavController(navController) }

@Stable
class TheNavController(
    val navController: NavHostController,
) {
  private val currentRoute: String?
    get() = navController.currentDestination?.route

  private val _currentRouteFlow: MutableStateFlow<String?> =
      MutableStateFlow(navController.currentDestination?.route)

  val currentRouteFlow: StateFlow<String?>
    get() = _currentRouteFlow

  init {
    navController.addOnDestinationChangedListener {
        _: NavController,
        navDestination: NavDestination,
        _: Bundle? ->
      _currentRouteFlow.value = navDestination.route
    }
  }

  fun upPress() {
    navController.navigateUp()
  }

  fun navigateToRoute(route: String, popToStart: Boolean = false, save_state: Boolean = true) {
    if (route != currentRoute) {
      navController.navigate(route) {
        launchSingleTop = true
        restoreState = true
        if (popToStart) {
          // Pop up backstack to the first destination and save state. This makes going back
          // to the start destination when pressing back in any other route
          popUpTo(findStartDestination(navController.graph).id) { saveState = save_state }
        }
      }
    }
  }
}

private val NavGraph.startDestination: NavDestination?
  get() = findNode(startDestinationId)

/**
 * Copied from similar function in NavigationUI.kt
 *
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:navigation/navigation-ui/src/main/java/androidx/navigation/ui/NavigationUI.kt
 */
private tailrec fun findStartDestination(graph: NavDestination): NavDestination {
  return if (graph is NavGraph) findStartDestination(graph.startDestination!!) else graph
}
