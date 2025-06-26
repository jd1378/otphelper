package io.github.jd1378.otphelper.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Modifier.verticalColumnScrollbar(
    scrollState: ScrollState,
    width: Dp = 4.dp,
    showScrollBarTrack: Boolean = true,
    scrollBarTrackColor: Color = MaterialTheme.colorScheme.surfaceDim,
    scrollBarColor: Color = MaterialTheme.colorScheme.primary,
    scrollBarCornerRadius: Float = 4f,
    endPadding: Float = 12f
): Modifier {
  return drawWithContent {
    drawContent()
    val viewportHeight = this.size.height
    val totalContentHeight = scrollState.maxValue.toFloat() + viewportHeight

    // Only draw scrollbar if content is scrollable
    if (totalContentHeight <= viewportHeight) return@drawWithContent

    val scrollValue = scrollState.value.toFloat()
    val scrollBarHeight = (viewportHeight / totalContentHeight) * viewportHeight
    val scrollBarStartOffset = (scrollValue / totalContentHeight) * viewportHeight

    if (showScrollBarTrack) {
      drawRoundRect(
          cornerRadius = CornerRadius(scrollBarCornerRadius),
          color = scrollBarTrackColor,
          topLeft = Offset(this.size.width - endPadding, 0f),
          size = Size(width.toPx(), viewportHeight),
      )
    }
    drawRoundRect(
        cornerRadius = CornerRadius(scrollBarCornerRadius),
        color = scrollBarColor,
        topLeft = Offset(this.size.width - endPadding, scrollBarStartOffset),
        size = Size(width.toPx(), scrollBarHeight))
  }
}

@Preview(showBackground = true, widthDp = 160)
@Composable
fun VerticalRowScrollbarPreview() {
  val scrollState = rememberScrollState()
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(100.dp)
              .verticalColumnScrollbar(scrollState)
              .verticalScroll(scrollState)
              .background(MaterialTheme.colorScheme.background)) {
        Column {
          repeat(6) { index ->
            Box(
                modifier =
                    Modifier.size(60.dp)
                        .padding(4.dp)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center) {
                  Text("Item $index")
                }
          }
        }
      }
}
