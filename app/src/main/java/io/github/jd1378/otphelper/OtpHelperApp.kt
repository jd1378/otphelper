package io.github.jd1378.otphelper

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import io.github.jd1378.otphelper.ui.navigation.rememberTheNavController
import io.github.jd1378.otphelper.ui.screens.home.addHomeGraph
import io.github.jd1378.otphelper.ui.screens.ignored_list.addIgnoredListGraph
import io.github.jd1378.otphelper.ui.screens.permissions.addPermissionsGraph
import io.github.jd1378.otphelper.ui.theme.OtpHelperTheme

@Composable
fun OtpHelperApp() {
  OtpHelperTheme {
    val theNavController = rememberTheNavController()

    NavHost(
        navController = theNavController.navController,
        startDestination = MainDestinations.HOME_ROUTE,
        enterTransition = {
          slideInHorizontally(
              initialOffsetX = { 300 },
              animationSpec = tween(300, easing = FastOutSlowInEasing),
          ) + fadeIn(animationSpec = tween(300))
        },
        exitTransition = {
          slideOutHorizontally(
              targetOffsetX = { -300 },
              animationSpec = tween(300, easing = FastOutSlowInEasing),
          ) + fadeOut(animationSpec = tween(300))
        },
        popEnterTransition = {
          slideInHorizontally(
              initialOffsetX = { -300 },
              animationSpec = tween(300, easing = FastOutSlowInEasing),
          ) + fadeIn(animationSpec = tween(300))
        },
        popExitTransition = {
          slideOutHorizontally(
              targetOffsetX = { 300 },
              animationSpec = tween(300, easing = FastOutSlowInEasing),
          ) + fadeOut(animationSpec = tween(300))
        },
    ) {
      otphelperNavGraph(
          upPress = theNavController::upPress,
          onNavigateToRoute = theNavController::navigateToRoute)
    }
  }
}

private fun NavGraphBuilder.otphelperNavGraph(
    upPress: () -> Unit,
    onNavigateToRoute: (String) -> Unit
) {
  addHomeGraph(onNavigateToRoute)
  addIgnoredListGraph(upPress)
  addPermissionsGraph(upPress)
  // for nested navigation (example):
  //  navigation(route = MainDestinations.HOME_ROUTE, startDestination =
  //          HomeSections.FEED.route) {
  //
  //  }
}
