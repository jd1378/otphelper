package io.github.jd1378.otphelper.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.utils.Clipboard

@Composable
fun CodeBlock(
    text: String,
) {
  val context = LocalContext.current
  val scrollState = rememberScrollState()
  Surface(
      color = MaterialTheme.colorScheme.surfaceContainerHigh, shape = MaterialTheme.shapes.medium) {
        Box(Modifier, contentAlignment = Alignment.Center) {
          Row(
              Modifier.padding(end = 46.dp)
                  .horizontalScrollbar(scrollState)
                  .horizontalScroll(scrollState)
                  .padding(dimensionResource(R.dimen.padding_small))) {
                SelectionContainer {
                  Text(
                      text = text,
                      fontWeight = FontWeight.Medium,
                      fontSize = 14.sp,
                      fontFamily = FontFamily.Monospace,
                      minLines = 1,
                      maxLines = 1,
                  )
                }
              }

          IconButton(
              onClick = { Clipboard.copyToClipboard(context, text) },
              modifier = Modifier.align(Alignment.CenterEnd)) {
                Icon(
                    painter = painterResource(R.drawable.baseline_content_copy_24),
                    contentDescription = stringResource(R.string.copy),
                )
              }
        }
      }
}
