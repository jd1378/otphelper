package io.github.jd1378.otphelper.ui.screens.ignored_list

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.AppImage
import io.github.jd1378.otphelper.ui.components.AppLabel
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

fun NavGraphBuilder.addIgnoredListGraph(upPress: () -> Unit) {
  composable(
      MainDestinations.IGNORED_LIST_ROUTE,
  ) {
    val viewModel = hiltViewModel<IgnoredListViewModel>()
    IgnoredList(upPress, viewModel)
  }
}

@Composable
fun IgnoredList(upPress: () -> Unit, viewModel: IgnoredListViewModel) {
  val ignoredNotifs = viewModel.ignoredNotifs.collectAsLazyPagingItems()
  val listState = rememberLazyListState()

  Scaffold(
      topBar = {
        TitleBar(
            upPress = upPress,
            text = LocalContext.current.getString(R.string.IGNORED_LIST_ROUTE),
        )
      }) { padding ->
        Column(
            Modifier.padding(padding).padding(dimensionResource(R.dimen.padding_page)),
        ) {
          Text(
              stringResource(R.string.ignored_list_description),
              modifier = Modifier.fillMaxWidth(),
              fontSize = 15.sp)

          HorizontalDivider(Modifier.padding(top = dimensionResource(R.dimen.padding_page)))

          Box(
              modifier =
                  Modifier.fillMaxSize().padding(top = dimensionResource(R.dimen.padding_medium)),
          ) {
            when (ignoredNotifs.loadState.refresh) {
              is LoadState.Loading ->
                  CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(40.dp))
              is LoadState.Error ->
                  Card {
                    Column(
                        Modifier.padding(vertical = 70.dp, horizontal = 20.dp).fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                      Text(stringResource(R.string.error))
                    }
                  }
              else -> {
                if (ignoredNotifs.itemCount == 0) {
                  Row {
                    Text(
                        stringResource(R.string.list_is_empty),
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                    )
                  }
                } else {
                  LazyColumn(
                      state = listState,
                      modifier = Modifier.fillMaxSize(),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement =
                          Arrangement.spacedBy(dimensionResource(R.dimen.padding_medium)),
                  ) {
                    items(
                        count = ignoredNotifs.itemCount,
                        key = ignoredNotifs.itemKey { it.packageName },
                    ) { index ->
                      val ignoredNotif = ignoredNotifs[index]
                      if (ignoredNotif != null) {
                        IgnoredListItem(ignoredNotif.packageName, ignoredNotif.totalItems) {
                          // TODO: move to ignored list detail
                        }
                      }
                    }
                    item {
                      if (ignoredNotifs.loadState.append == LoadState.Loading) {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
}

@Composable
fun IgnoredListItem(packageName: String, totalItems: Long, onClick: (packageName: String) -> Unit) {
  ListItem(
      modifier = Modifier.clip(MaterialTheme.shapes.large).clickable { onClick(packageName) },
      leadingContent = {
        AppImage(
            packageName,
            modifier =
                Modifier.size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, Color(0.5f, 0.5f, 0.5f, 0.5f), RoundedCornerShape(10.dp)),
        )
      },
      headlineContent = { AppLabel(packageName) },
      supportingContent = {
        Text(
            text = stringResource(R.string.n_items, totalItems),
            color = MaterialTheme.colorScheme.primary,
        )
      },
      trailingContent = {
        Icon(
            painterResource(R.drawable.baseline_navigate_next_24),
            null,
        )
      },
      colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
  )
}
