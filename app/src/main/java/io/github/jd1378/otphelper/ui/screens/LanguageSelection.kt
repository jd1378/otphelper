package io.github.jd1378.otphelper.ui.screens

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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.TitleBar

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
          val onActiveChange = { _: Boolean -> }
          val colors1 = SearchBarDefaults.colors()
          SearchBar(
              inputField = {
                SearchBarDefaults.InputField(
                    // prevent saying "search" twice in talkback
                    modifier = Modifier.semantics { contentDescription = "" },
                    query = uiState.searchTerm,
                    onQueryChange = { viewModel.setSearchTerm(it) },
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = onActiveChange,
                    enabled = true,
                    placeholder = { Text(text = stringResource(R.string.search_language)) },
                    leadingIcon = {
                      Icon(
                          imageVector = Icons.Default.Search,
                          contentDescription = null,
                      )
                    },
                )
              },
              expanded = false,
              onExpandedChange = onActiveChange,
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(horizontal = dimensionResource(R.dimen.padding_medium))
                      .padding(bottom = dimensionResource(R.dimen.padding_xs)),
              shape = SearchBarDefaults.inputFieldShape,
              colors = colors1,
              tonalElevation = SearchBarDefaults.TonalElevation,
              shadowElevation = SearchBarDefaults.ShadowElevation,
              windowInsets = SearchBarDefaults.windowInsets,
              content = {},
          )
          LazyColumn(
              Modifier.padding(horizontal = dimensionResource(R.dimen.padding_small)),
          ) {
            items(items = uiState.locales, key = { locale -> locale.code }) { locale ->
              Row(
                  modifier =
                      Modifier.animateItem(fadeOutSpec = null)
                          .fillMaxWidth()
                          .clickable(onClick = { viewModel.selectLocale(locale) })) {
                    Text(
                        text = stringResource(locale.label),
                        Modifier.fillMaxWidth()
                            .padding(
                                horizontal = dimensionResource(R.dimen.padding_small),
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
                                horizontal = dimensionResource(R.dimen.padding_small),
                                vertical = 14.dp))
              }
            }
          }
        }
      }
}
