package io.github.jd1378.otphelper.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.DangerousActionDialog
import io.github.jd1378.otphelper.ui.components.NewPhraseDialog
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.components.drawVerticalScrollbar
import io.github.jd1378.otphelper.utils.Clipboard

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun IgnoredPhrases(
    modifier: Modifier = Modifier,
    upPress: () -> Unit,
    viewModel: IgnoredPhrasesViewModel
) {
  val uriHandler = LocalUriHandler.current
  val context = LocalContext.current

  val phrases by viewModel.ignoredPhrases.collectAsStateWithLifecycle()
  val listState = rememberLazyListState()
  val showResetToDefaultDialog = viewModel.showResetToDefaultDialog.collectAsStateWithLifecycle()
  val showNewIgnoredPhraseDialog =
      viewModel.showNewIgnoredPhraseDialog.collectAsStateWithLifecycle()
  val showClearListDialog = viewModel.showClearListDialog.collectAsStateWithLifecycle()

  Scaffold(
      modifier = modifier,
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.ignored_phrases),
        ) {
          var expanded by remember { mutableStateOf(false) }

          Box(modifier = Modifier.wrapContentSize(Alignment.TopStart)) {
            IconButton(onClick = { expanded = true }) {
              Icon(Icons.Default.MoreVert, contentDescription = stringResource(R.string.options))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
              DropdownMenuItem(
                  text = { Text(stringResource(R.string.reset_to_default)) },
                  onClick = {
                    viewModel.showResetToDefaultDialog.value = true
                    expanded = false
                  },
                  leadingIcon = {
                    Icon(
                        painterResource(R.drawable.baseline_settings_backup_restore_24),
                        contentDescription = null)
                  },
              )
              DropdownMenuItem(
                  text = { Text(stringResource(R.string.clear_list)) },
                  onClick = {
                    viewModel.showClearListDialog.value = true
                    expanded = false
                  },
                  leadingIcon = { Icon(Icons.Default.Delete, contentDescription = null) },
              )
              HorizontalDivider()
              DropdownMenuItem(
                  text = { Text(stringResource(R.string.copy_ignore_regex)) },
                  onClick = {
                    Clipboard.copyToClipboard(
                        context,
                        viewModel.autoUpdatingListenerUtils.codeExtractor
                            ?.ignoredPhrasesRegex
                            ?.toString() ?: "",
                        false)
                    expanded = false
                  },
                  leadingIcon = {
                    Icon(
                        painterResource(R.drawable.baseline_content_copy_24),
                        contentDescription = null)
                  },
              )
            }
          }
        }
      },
      floatingActionButton = {
        FloatingActionButton(
            onClick = { viewModel.showNewIgnoredPhraseDialog.value = true },
        ) {
          Icon(Icons.Filled.Add, stringResource(R.string.add))
        }
      },
  ) { padding ->
    Column(
        Modifier.padding(padding)
            .padding(horizontal = dimensionResource(R.dimen.padding_page))
            .padding(top = dimensionResource(R.dimen.padding_page)),
    ) {
      if (showResetToDefaultDialog.value) {
        DangerousActionDialog(
            stringResource(R.string.reset_to_default),
            onDismissRequest = { viewModel.showResetToDefaultDialog.value = false },
        ) {
          viewModel.resetToDefault()
        }
      }
      if (showClearListDialog.value) {
        DangerousActionDialog(
            stringResource(R.string.clear_list),
            onDismissRequest = { viewModel.showClearListDialog.value = false },
        ) {
          viewModel.clearList()
        }
      }
      if (showNewIgnoredPhraseDialog.value) {
        NewPhraseDialog(
            title = stringResource(R.string.new_ignored_phrase),
            validationPredicate = viewModel::isIgnoredPhraseParsable,
            onDismissRequest = { viewModel.showNewIgnoredPhraseDialog.value = false },
        ) {
          viewModel.addNewPhrase(it)
        }
      }

      val hyperlinkText = stringResource(R.string.learn_more)
      val annotatedString = buildAnnotatedString {
        pushStyle(SpanStyle(fontSize = 15.sp, color = MaterialTheme.colorScheme.onBackground))
        append(stringResource(R.string.ignored_phrases_desc))
        append(" ")
        append(stringResource(R.string.sensitive_phrases_desc_regex))
        append(" ")
        withStyle(
            style =
                SpanStyle(
                    textDecoration = TextDecoration.Underline,
                    color = MaterialTheme.colorScheme.primary)) {
              append(hyperlinkText)
              addStringAnnotation(
                  tag = "URL",
                  annotation = "https://regextutorial.org/",
                  start = length - hyperlinkText.length,
                  end = length)
            }
      }

      ClickableText(
          text = annotatedString,
          onClick = { offset ->
            annotatedString
                .getStringAnnotations(tag = "URL", start = offset, end = offset)
                .firstOrNull()
                ?.let { annotation -> uriHandler.openUri(annotation.item) }
          },
          modifier = Modifier.fillMaxWidth(),
      )

      HorizontalDivider(Modifier.padding(top = dimensionResource(R.dimen.padding_page)))

      Box(
          modifier =
              Modifier.fillMaxSize()
                  .padding(top = dimensionResource(R.dimen.padding_page))
                  .drawVerticalScrollbar(listState),
      ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
          itemsIndexed(phrases, key = { _, item -> item }) { index, phrase ->
            Row(
                Modifier.fillMaxWidth().animateItemPlacement(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
              Text(phrase, modifier = Modifier.weight(1f))
              IconButton(onClick = { viewModel.deletePhrase(index) }) {
                Icon(Icons.Default.Delete, stringResource(R.string.delete))
              }
            }
          }
          item { Spacer(Modifier.padding(40.dp)) }
        }
      }
    }
  }
}
