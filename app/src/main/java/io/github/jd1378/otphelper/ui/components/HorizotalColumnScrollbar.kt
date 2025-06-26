package io.github.jd1378.otphelper.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
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
fun Modifier.horizontalScrollbar(
    scrollState: ScrollState,
    height: Dp = 4.dp,
    showScrollBarTrack: Boolean = true,
    scrollBarTrackColor: Color = MaterialTheme.colorScheme.surfaceDim,
    scrollBarColor: Color = MaterialTheme.colorScheme.primary,
    scrollBarCornerRadius: Float = 4f,
    endPadding: Float = 12f
): Modifier {
  return drawWithContent {
    drawContent()
    val viewportWidth = this.size.width
    val totalContentWidth = scrollState.maxValue.toFloat() + viewportWidth

    // Only draw scrollbar if content is scrollable
    if (totalContentWidth <= viewportWidth) return@drawWithContent

    val scrollValue = scrollState.value.toFloat()
    val scrollBarWidth = (viewportWidth / totalContentWidth) * viewportWidth
    val scrollBarStartOffset = (scrollValue / totalContentWidth) * viewportWidth

    if (showScrollBarTrack) {
      drawRoundRect(
          cornerRadius = CornerRadius(scrollBarCornerRadius),
          color = scrollBarTrackColor,
          topLeft = Offset(0f, this.size.height - endPadding),
          size = Size(viewportWidth, height.toPx()),
      )
    }
    drawRoundRect(
        cornerRadius = CornerRadius(scrollBarCornerRadius),
        color = scrollBarColor,
        topLeft = Offset(scrollBarStartOffset, this.size.height - endPadding),
        size = Size(scrollBarWidth, height.toPx()))
  }
}

@Preview(showBackground = true)
@Composable
fun HorizontalScrollbarModifierPreview() {
  val scrollState = rememberScrollState()
  Box(
      modifier =
          Modifier.fillMaxWidth()
              .height(80.dp)
              .horizontalScrollbar(scrollState)
              .horizontalScroll(scrollState)
              .background(MaterialTheme.colorScheme.background)) {
        Row {
          repeat(20) { index ->
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
