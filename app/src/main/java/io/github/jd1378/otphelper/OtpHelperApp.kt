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
import androidx.navigation.compose.NavHost
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import io.github.jd1378.otphelper.ui.navigation.otphelperNavGraph
import io.github.jd1378.otphelper.ui.navigation.rememberTheNavController
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
