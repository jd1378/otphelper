package io.github.jd1378.otphelper.ui.screens.ignored_list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
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
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.model.getTranslation
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
            Modifier.padding(padding)
                .padding(horizontal = dimensionResource(R.dimen.medium_padding)),
        ) {
          Text(
              stringResource(R.string.ignored_list_description),
              modifier = Modifier.fillMaxWidth(),
              fontSize = 15.sp)

          Box(
              modifier =
                  Modifier.fillMaxSize().padding(top = dimensionResource(R.dimen.medium_padding)),
          ) {
            when (ignoredNotifs.loadState.refresh) {
              is LoadState.Loading ->
                  CircularProgressIndicator(modifier = Modifier.align(Alignment.Center).size(40.dp))
              is LoadState.Error ->
                  Card {
                    Column(Modifier.padding(vertical = 70.dp, horizontal = 20.dp)) {
                      Text(stringResource(R.string.error))
                    }
                  }
              else -> {
                if (ignoredNotifs.itemCount == 0) {
                  Row(Modifier.padding(padding)) {
                    Text(
                        stringResource(R.string.list_is_empty),
                        modifier = Modifier.fillMaxSize(),
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center)
                  }
                } else {
                  LazyColumn(
                      state = listState,
                      modifier = Modifier.fillMaxSize(),
                      horizontalAlignment = Alignment.CenterHorizontally,
                      verticalArrangement =
                          Arrangement.spacedBy(dimensionResource(R.dimen.medium_padding)),
                  ) {
                    items(
                        count = ignoredNotifs.itemCount,
                        key = ignoredNotifs.itemKey { it.id },
                    ) { index ->
                      val ignoredNotif = ignoredNotifs[index]
                      if (ignoredNotif != null) {
                        IgnoredListItem(ignoredNotif) { viewModel.removeIgnoredNotif(ignoredNotif) }
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
fun IgnoredListItem(ignoredNotif: IgnoredNotif, delete: (ignoredNotif: IgnoredNotif) -> Unit) {
  Row(
      modifier = Modifier.padding(5.dp).fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    AppImage(ignoredNotif.packageName, modifier = Modifier.size(64.dp))
    Spacer(Modifier.size(10.dp))
    Column(
        modifier = Modifier.width(0.dp).weight(1f).fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.extra_small_padding)),
    ) {
      AppLabel(ignoredNotif.packageName)
      Row(
          horizontalArrangement =
              Arrangement.spacedBy(dimensionResource(R.dimen.extra_small_padding)),
      ) {
        Text(text = ignoredNotif.type.getTranslation())
        if (!ignoredNotif.typeData.isNullOrEmpty()) {
          Text(
              text = "(" + ignoredNotif.typeData + ")",
          )
        }
      }
    }

    IconButton(onClick = { delete(ignoredNotif) }) {
      Icon(
          imageVector = Icons.Filled.Delete,
          contentDescription = stringResource(R.string.delete),
      )
    }
  }
}
