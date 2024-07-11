package io.github.jd1378.otphelper.ui.screens

import android.text.format.DateUtils
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.AppImage
import io.github.jd1378.otphelper.ui.components.IgnoreAppButton
import io.github.jd1378.otphelper.ui.components.IgnoreNotifIdButton
import io.github.jd1378.otphelper.ui.components.IgnoreNotifTagButton
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.components.getAppInfo
import io.github.jd1378.otphelper.ui.theme.LocalCustomColors
import io.github.jd1378.otphelper.ui.utils.SkipFirstLaunchedEffect

@Composable
fun HistoryDetail(
    modifier: Modifier = Modifier,
    upPress: () -> Unit,
    viewModel: HistoryDetailViewModel
) {
  val state = viewModel.detectedCode.collectAsStateWithLifecycle()
  val isAppIgnored = viewModel.isAppIgnored.collectAsStateWithLifecycle()
  val isNotifIdIgnored = viewModel.isNotifIdIgnored.collectAsStateWithLifecycle()
  val isNotifTagIgnored = viewModel.isNotifTagIgnored.collectAsStateWithLifecycle()
  val codeExtractorResult = remember {
    derivedStateOf {
      viewModel.autoUpdatingListenerUtils.codeExtractor?.getCodeMatch(state.value?.text)
    }
  }
  val context = LocalContext.current
  val scrollState = rememberScrollState()

  SkipFirstLaunchedEffect(state.value) {
    if (state.value == null) {
      upPress()
    }
  }

  Scaffold(
      modifier = modifier,
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.history_detail),
        )
      },
  ) { padding ->
    if (state.value != null) {
      val detectedCode = state.value!!
      val appInfoResult =
          remember(detectedCode.packageName) { getAppInfo(context, detectedCode.packageName) }

      Column(
          Modifier.padding(padding)
              .padding(horizontal = dimensionResource(R.dimen.padding_page))
              .padding(top = dimensionResource(R.dimen.padding_page))
              .verticalScroll(scrollState),
          verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
      ) {
        if (appInfoResult.failed) {
          Text(
              stringResource(R.string.app_label_not_visible_reason),
              fontSize = 14.sp,
              textAlign = TextAlign.Center,
              modifier =
                  Modifier.fillMaxWidth()
                      .padding(vertical = dimensionResource(R.dimen.padding_settings)),
              color = MaterialTheme.colorScheme.tertiary,
          )
        }

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Row(
              Modifier.weight(1f),
              horizontalArrangement =
                  Arrangement.spacedBy(
                      dimensionResource(R.dimen.padding_page),
                  ),
          ) {
            AppImage(
                appInfoResult.icon,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(10.dp)),
            )

            Column() {
              Text(
                  text = appInfoResult.appLabel,
                  fontWeight = FontWeight.Medium,
                  fontSize = 16.sp,
              )

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
            }
          }

          IgnoreAppButton(isAppIgnored.value) { viewModel.toggleAppIgnore(detectedCode) }
        }

        HorizontalDivider()

        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
          Column(Modifier.weight(1f)) {
            Text(
                stringResource(R.string.notification_id) + ": " + detectedCode.notificationId,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
            )
          }

          IgnoreNotifIdButton(isNotifIdIgnored.value) {
            viewModel.toggleNotifIdIgnore(detectedCode)
          }
        }

        if (detectedCode.notificationTag.isNotBlank()) {
          HorizontalDivider()

          Row(
              Modifier.fillMaxWidth(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween,
          ) {
            Column(Modifier.weight(1f)) {
              Text(
                  stringResource(R.string.notification_tag) + ":",
                  fontWeight = FontWeight.Medium,
                  fontSize = 16.sp,
              )
              Text(detectedCode.notificationTag)
            }

            IgnoreNotifTagButton(isNotifTagIgnored.value) {
              viewModel.toggleNotifTagIgnore(detectedCode)
            }
          }
        }

        HorizontalDivider()

        val customColors = LocalCustomColors.current

        Column() {
          SelectionContainer {
            Text(
                buildAnnotatedString {
                  append(detectedCode.text)
                  if (codeExtractorResult.value != null) {
                    val phraseGroupRange =
                        codeExtractorResult.value!!
                            .matchResult
                            .groups[codeExtractorResult.value!!.phraseGroup]!!
                            .range
                    val codeGroupRange =
                        codeExtractorResult.value!!
                            .matchResult
                            .groups[codeExtractorResult.value!!.codeGroup]!!
                            .range
                    addStyle(
                        style =
                            SpanStyle(
                                color = customColors.phraseHighlight,
                                fontWeight = FontWeight.ExtraBold,
                            ),
                        start = phraseGroupRange.first,
                        end = phraseGroupRange.last + 1)
                    addStyle(
                        style =
                            SpanStyle(
                                color = customColors.codeHighlight,
                                fontWeight = FontWeight.ExtraBold,
                            ),
                        start = codeGroupRange.first,
                        end = codeGroupRange.last + 1)
                  }
                },
            )
          }
        }
      }
    }
  }
}
