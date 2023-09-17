package io.github.jd1378.otphelper.ui.screens.ignored_list

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jd1378.otphelper.R
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
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  Scaffold(
      topBar = {
        TitleBar(
            upPress = upPress,
            text = LocalContext.current.getString(R.string.IGNORED_LIST_ROUTE),
        )
      }) { padding ->
        Column(Modifier.padding(padding)) {
          Text(
              stringResource(R.string.ignored_list_description),
              modifier = Modifier.fillMaxWidth().padding(PaddingValues(10.dp)),
              fontSize = 15.sp)

          if (uiState.ignoredNotifs.isNotEmpty()) {
            LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(10.dp)) {
              items(items = uiState.ignoredNotifs, key = { i -> i }) { ignoredNotif ->
                WordListItem(ignoredNotif) { viewModel.removeIgnoredNotif(ignoredNotif) }
              }
            }
          } else {
            Row(Modifier.padding(padding)) {
              Text(
                  stringResource(R.string.list_is_empty),
                  modifier = Modifier.fillMaxSize(),
                  fontSize = 25.sp,
                  textAlign = TextAlign.Center)
            }
          }
        }
      }
}

@Composable
fun WordListItem(word: String, delete: (word: String) -> Unit) {
  Row(
      modifier = Modifier.padding(5.dp).fillMaxWidth(),
      verticalAlignment = Alignment.CenterVertically) {
        Text(text = word, Modifier.width(0.dp).weight(1f).fillMaxWidth())
        IconButton(onClick = { delete(word) }) {
          Icon(
              imageVector = Icons.Filled.Delete,
              contentDescription = stringResource(R.string.delete),
          )
        }
      }
}
