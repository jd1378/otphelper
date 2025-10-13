package io.github.jd1378.otphelper.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.components.drawVerticalScrollbar
import io.github.jd1378.otphelper.ui.theme.LocalCustomColors
import io.github.jd1378.otphelper.utils.CodeExtractorResult
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun DetectionTest(
    modifier: Modifier = Modifier,
    upPress: () -> Unit,
    viewModel: DetectionTestViewModel,
) {
  val scope = rememberCoroutineScope()
  val scrollState = rememberScrollState()
  var detectionTestContent by remember { mutableStateOf("") }
  var isInitialLoad by remember { mutableStateOf(true) }
  var ignoredPhrase by remember { mutableStateOf<String?>(null) }
  var cleanedUpContent by remember { mutableStateOf<String?>(null) }
  var extractResult by remember { mutableStateOf<CodeExtractorResult?>(null) }

  LaunchedEffect(Unit) {
    scope.launch {
      detectionTestContent = viewModel.getSavedDetectionTestContent()
      isInitialLoad = false
    }
  }

  LaunchedEffect(detectionTestContent) {
    if (isInitialLoad) return@LaunchedEffect
    // effectively debounce
    delay(300)
    viewModel.saveDetectionTestContent(detectionTestContent)
    viewModel.autoUpdatingListenerUtils.codeExtractor?.let { extractor ->
      ignoredPhrase = extractor.getIgnorePhrase(detectionTestContent)
      cleanedUpContent =
          viewModel.autoUpdatingListenerUtils.codeExtractor?.cleanup(detectionTestContent)
      extractResult =
          viewModel.autoUpdatingListenerUtils.codeExtractor?.getCodeMatch(cleanedUpContent)
    }
  }

  Scaffold(
      modifier = modifier,
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.detection_test),
        )
      },
  ) { padding ->
    Column(
        Modifier.padding(padding)
            .imePadding()
            .padding(horizontal = dimensionResource(R.dimen.padding_page))
            .padding(top = dimensionResource(R.dimen.padding_page)),
        verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
    ) {
      Text(stringResource(R.string.detection_test_desc))

      HorizontalDivider()

      val customColors = LocalCustomColors.current

      OutlinedTextField(
          modifier = Modifier.weight(1f).fillMaxWidth(),
          value = detectionTestContent,
          onValueChange = { detectionTestContent = it },
      )

      if (ignoredPhrase.isNullOrBlank()) {
        val result = extractResult
        if (result != null) {
          SelectionContainer {
            Text(
                buildAnnotatedString {
                  append(stringResource(R.string.detected_code))
                  append(" ")
                  withStyle(
                      SpanStyle(
                          color = customColors.codeHighlight,
                          fontWeight = FontWeight.ExtraBold,
                      )
                  ) {
                    append(result.matchResult.groups[result.codeGroup]!!.value)
                  }
                }
            )
          }
        }
      }
      HorizontalDivider()

      SelectionContainer(
          Modifier.weight(1f)
              .fillMaxWidth()
              .verticalScroll(scrollState)
              .drawVerticalScrollbar(scrollState)
      ) {
        Text(
            buildAnnotatedString {
              if (!ignoredPhrase.isNullOrBlank()) {
                withStyle(style = SpanStyle(MaterialTheme.colorScheme.error)) {
                  append(stringResource(R.string.detection_test_ignore_phrase_detected))
                  append(": ")
                  append(ignoredPhrase)
                }
              } else if (cleanedUpContent.isNullOrBlank()) {
                append(stringResource(R.string.detection_test_empty_hint))
              } else {
                append(cleanedUpContent)
                val result = extractResult
                if (result != null) {
                  val phraseGroupRange = result.matchResult.groups[result.phraseGroup]!!.range
                  val codeGroupRange = result.matchResult.groups[result.codeGroup]!!.range
                  addStyle(
                      style =
                          SpanStyle(
                              color = customColors.phraseHighlight,
                              fontWeight = FontWeight.ExtraBold,
                          ),
                      start = phraseGroupRange.first,
                      end = phraseGroupRange.last + 1,
                  )
                  addStyle(
                      style =
                          SpanStyle(
                              color = customColors.codeHighlight,
                              fontWeight = FontWeight.ExtraBold,
                          ),
                      start = codeGroupRange.first,
                      end = codeGroupRange.last + 1,
                  )
                }
              }
            },
        )
      }
    }
  }
}
