package io.github.jd1378.otphelper

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import io.github.jd1378.otphelper.ui.navigation.rememberTheNavController
import io.github.jd1378.otphelper.ui.screens.about.addAboutGraph
import io.github.jd1378.otphelper.ui.screens.history.addHistoryGraph
import io.github.jd1378.otphelper.ui.screens.historydetail.addHistoryDetailGraph
import io.github.jd1378.otphelper.ui.screens.home.addHomeGraph
import io.github.jd1378.otphelper.ui.screens.ignored_app_list.addIgnoredAppListGraph
import io.github.jd1378.otphelper.ui.screens.language_selection.addLanguageSelectionGraph
import io.github.jd1378.otphelper.ui.screens.permissions.addPermissionsGraph
import io.github.jd1378.otphelper.ui.screens.settings.addSettingsGraph
import io.github.jd1378.otphelper.ui.theme.OtpHelperTheme

@Composable
fun OtpHelperApp(
    deepLinkHandler: DeepLinkHandler,
) {
  OtpHelperTheme {
    val theNavController = rememberTheNavController()
    val event by deepLinkHandler.event.collectAsState()

    LaunchedEffect(event) {
      when (val currentEvent = event) {
        is Event.NavigateWithDeepLink ->
            theNavController.navController.handleDeepLink(currentEvent.intent)
        Event.None -> Unit
      }
      deepLinkHandler.consumeEvent()
    }

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
  // for nested navigation (example):
  //  navigation(route = MainDestinations.HOME_ROUTE, startDestination =
  //          HomeSections.FEED.route) {
  //
  //  }
}
