package io.github.jd1378.otphelper.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.data.local.entity.DetectedCode
import io.github.jd1378.otphelper.ui.components.AppImage
import io.github.jd1378.otphelper.ui.components.DangerousActionDialog
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.components.drawVerticalScrollbar
import io.github.jd1378.otphelper.ui.components.getAppLabel
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

@Composable
fun History(
    modifier: Modifier = Modifier,
    onNavigateToRoute: (String, Boolean) -> Unit,
    upPress: () -> Unit,
    viewModel: HistoryViewModel
) {
  val historyItems = viewModel.historyItems.collectAsLazyPagingItems()
  val listState = rememberLazyListState()
  val showClearHistoryDialog = viewModel.showClearHistoryDialog.collectAsStateWithLifecycle()

  Scaffold(
      modifier = modifier,
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.history),
        ) {
          var expanded by remember { mutableStateOf(false) }

          Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            IconButton(onClick = { expanded = true }) {
              Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.options))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              DropdownMenuItem(
                  text = { Text(stringResource(R.string.clear_history)) },
                  onClick = {
                    viewModel.showClearHistoryDialog.value = true
                    expanded = false
                  },
                  leadingIcon = { Icon(Icons.Outlined.Delete, contentDescription = null) })
            }
          }
        }
      },
  ) { padding ->
    Column(
        Modifier.padding(padding)
            .padding(horizontal = dimensionResource(R.dimen.padding_page))
            .padding(top = dimensionResource(R.dimen.padding_page)),
    ) {
      if (showClearHistoryDialog.value) {
        DangerousActionDialog(
            stringResource(R.string.clear_history),
            onDismissRequest = { viewModel.showClearHistoryDialog.value = false }) {
              viewModel.clearHistory()
            }
      }

      Text(
          stringResource(R.string.history_desc),
          modifier = Modifier.fillMaxWidth(),
          fontSize = 15.sp)

      HorizontalDivider(Modifier.padding(top = dimensionResource(R.dimen.padding_page)))

      Box(
          modifier =
              Modifier.fillMaxSize()
                  .padding(top = dimensionResource(R.dimen.padding_page))
                  .drawVerticalScrollbar(listState),
      ) {
        when (historyItems.loadState.refresh) {
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
            if (historyItems.itemCount == 0) {
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
                    count = historyItems.itemCount,
                    key = historyItems.itemKey { it.id },
                ) { index ->
                  val detectedCode = historyItems[index]!! // because enablePlaceholders = false
                  DetectedCodeListItem(
                      Modifier.clickable {
                        onNavigateToRoute(
                            MainDestinations.HISTORY_DETAIL_ROUTE + "/" + detectedCode.id, false)
                      },
                      detectedCode)
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
fun DetectedCodeListItem(modifier: Modifier = Modifier, detectedCode: DetectedCode) {
  val context = LocalContext.current
  val appLabel =
      remember(detectedCode.packageName) { getAppLabel(context, detectedCode.packageName).label }

  ListItem(
      modifier = Modifier.clip(MaterialTheme.shapes.large).then(modifier),
      leadingContent = {
        AppImage(
            detectedCode.packageName,
            modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)),
        )
      },
      headlineContent = { Text(appLabel) },
      supportingContent = {
        Text(
            text =
                DateUtils.getRelativeTimeSpanString(
                        detectedCode.createdAt.time,
                        System.currentTimeMillis(),
                        0L,
                        DateUtils.FORMAT_ABBREV_ALL)
                    .toString(),
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
