package io.github.jd1378.otphelper.ui.screens.language_selection

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

fun NavGraphBuilder.addLanguageSelectionGraph(upPress: () -> Unit) {
  composable(
      MainDestinations.LANGUAGE_SELECTION_ROUTE,
  ) {
    val viewModel = hiltViewModel<LanguageSelectionViewModel>()
    LanguageSelection(upPress, viewModel)
  }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelection(upPress: () -> Unit, viewModel: LanguageSelectionViewModel) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
      topBar = {
        TitleBar(
            upPress = upPress,
            text = LocalContext.current.getString(R.string.language),
        )
      }) { padding ->
        Column(Modifier.padding(padding)) {
          SearchBar(
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = dimensionResource(R.dimen.medium_padding))
                      .padding(bottom = dimensionResource(R.dimen.extra_small_padding)),
              query = uiState.searchTerm,
              onSearch = {},
              onQueryChange = { viewModel.setSearchTerm(it) },
              active = false,
              onActiveChange = {},
              placeholder = { Text(text = stringResource(R.string.search_language)) },
              leadingIcon = {
                Icon(imageVector = Icons.Default.Search, contentDescription = null)
              }) {}
          LazyColumn(
              Modifier.padding(horizontal = dimensionResource(R.dimen.small_padding)),
          ) {
            items(items = uiState.locales, key = { locale -> locale.code }) { locale ->
              Row(
                  modifier =
                      Modifier.animateItemPlacement()
                          .fillMaxWidth()
                          .clickable(onClick = { viewModel.selectLocale(locale) })) {
                    Text(
                        text = stringResource(locale.label),
                        Modifier.fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(R.dimen.small_padding),
                                vertical = 14.dp))
                  }
            }
            if (uiState.locales.isEmpty()) {
              item {
                Text(
                    text = stringResource(R.string.no_result),
                    textAlign = TextAlign.Center,
                    modifier =
                        Modifier.fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(R.dimen.small_padding),
                                vertical = 14.dp))
              }
            }
          }
        }
      }
}
