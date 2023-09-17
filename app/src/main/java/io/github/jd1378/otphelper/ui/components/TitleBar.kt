package io.github.jd1378.otphelper.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.utils.mirroringBackIcon

@Composable
private fun Up(upPress: () -> Unit) {
  IconButton(
      onClick = upPress,
      modifier =
          Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp).size(36.dp)) {
        Icon(
            imageVector = mirroringBackIcon(),
            contentDescription = stringResource(R.string.label_back))
      }
}

@Composable
fun TitleBar(
    upPress: () -> Unit,
    text: String,
    showBackBtn: Boolean = true,
    content: @Composable () -> Unit = {}
) {
  Row(
      Modifier.fillMaxWidth().statusBarsPadding().padding(top = 5.dp),
      verticalAlignment = Alignment.CenterVertically,
  ) {
    if (showBackBtn) {
      Up(upPress)
    } else {
      Box(Modifier.width(10.dp).height(56.dp))
    }
    Text(text = text, fontSize = 20.sp, modifier = Modifier.padding(end = 15.dp))
    Spacer(Modifier.weight(1f))
    content()
  }
}
