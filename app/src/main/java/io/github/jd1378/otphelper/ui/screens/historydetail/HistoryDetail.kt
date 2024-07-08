package io.github.jd1378.otphelper.ui.screens.historydetail

import android.text.format.DateUtils
import androidx.compose.foundation.border
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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import io.github.jd1378.otphelper.OTPHELPER_APP_SCHEME
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.AppImage
import io.github.jd1378.otphelper.ui.components.AppLabel
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import io.github.jd1378.otphelper.ui.navigation.NavArgs
import io.github.jd1378.otphelper.ui.theme.LocalCustomColors
import io.github.jd1378.otphelper.ui.utils.SkipFirstLaunchedEffect
import io.github.jd1378.otphelper.utils.CodeExtractor

fun NavGraphBuilder.addHistoryDetailGraph(modifier: Modifier = Modifier, upPress: () -> Unit) {
  composable(
      "${MainDestinations.HISTORY_DETAIL_ROUTE}/{${NavArgs.HISTORY_ID}}",
      deepLinks =
          listOf(
              navDeepLink {
                uriPattern =
                    "$OTPHELPER_APP_SCHEME://${MainDestinations.HISTORY_DETAIL_ROUTE}/{${NavArgs.HISTORY_ID}}"
              }),
      arguments = listOf(navArgument(NavArgs.HISTORY_ID) { type = NavType.LongType })) {
          backStackEntry ->
        val historyId = backStackEntry.arguments?.getLong(NavArgs.HISTORY_ID)
        if (historyId == 0L) {
          upPress()
        } else {
          val viewModel = hiltViewModel<HistoryDetailViewModel>()
          HistoryDetail(modifier, upPress, viewModel)
        }
      }
}

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
    derivedStateOf { CodeExtractor.getCodeMatch(state.value?.text) }
  }

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
      Column(
          Modifier.padding(padding)
              .padding(horizontal = dimensionResource(R.dimen.padding_page))
              .padding(top = dimensionResource(R.dimen.padding_page))
              .verticalScroll(scrollState),
          verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_settings)),
      ) {
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
                detectedCode.packageName,
                modifier =
                    Modifier.size(64.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.dp, Color(0.5f, 0.5f, 0.5f, 0.5f), RoundedCornerShape(10.dp)),
            )

            Column() {
              AppLabel(
                  detectedCode.packageName,
                  textStyle =
                      LocalTextStyle.current.copy(
                          fontWeight = FontWeight.Medium,
                          fontSize = 16.sp,
                      ))

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

          OutlinedButton(
              onClick = { viewModel.toggleAppIgnore(detectedCode) },
              colors =
                  if (isAppIgnored.value) ButtonDefaults.outlinedButtonColors()
                  else
                      ButtonDefaults.outlinedButtonColors()
                          .copy(
                              contentColor = MaterialTheme.colorScheme.error,
                          ),
          ) {
            if (isAppIgnored.value) {
              Text(stringResource(R.string.allow_app))
            } else {
              Text(stringResource(R.string.ignore_app))
            }
          }
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

          OutlinedButton(
              onClick = { viewModel.toggleNotifIdIgnore(detectedCode) },
              colors =
                  if (isNotifIdIgnored.value) ButtonDefaults.outlinedButtonColors()
                  else
                      ButtonDefaults.outlinedButtonColors()
                          .copy(
                              contentColor = MaterialTheme.colorScheme.error,
                          ),
          ) {
            if (isNotifIdIgnored.value) {
              Text(stringResource(R.string.allow_id))
            } else {
              Text(stringResource(R.string.ignore_id))
            }
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

            OutlinedButton(
                onClick = { viewModel.toggleNotifTagIgnore(detectedCode) },
                colors =
                    if (isNotifTagIgnored.value) ButtonDefaults.outlinedButtonColors()
                    else
                        ButtonDefaults.outlinedButtonColors()
                            .copy(
                                contentColor = MaterialTheme.colorScheme.error,
                            ),
            ) {
              if (isNotifTagIgnored.value) {
                Text(stringResource(R.string.allow_tag))
              } else {
                Text(stringResource(R.string.ignore_tag))
              }
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
