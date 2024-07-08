package io.github.jd1378.otphelper.ui.navigation

import androidx.navigation.NavGraphBuilder
import io.github.jd1378.otphelper.ui.screens.about.addAboutGraph
import io.github.jd1378.otphelper.ui.screens.history.addHistoryGraph
import io.github.jd1378.otphelper.ui.screens.historydetail.addHistoryDetailGraph
import io.github.jd1378.otphelper.ui.screens.home.addHomeGraph
import io.github.jd1378.otphelper.ui.screens.ignored_app_list.addIgnoredAppListGraph
import io.github.jd1378.otphelper.ui.screens.language_selection.addLanguageSelectionGraph
import io.github.jd1378.otphelper.ui.screens.permissions.addPermissionsGraph
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
  // for nested navigation (example):
  //  navigation(route = MainDestinations.HOME_ROUTE, startDestination =
  //          HomeSections.FEED.route) {
  //
  //  }
}
