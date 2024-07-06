package io.github.jd1378.otphelper.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.jd1378.otphelper.R

@Composable
private fun Up(upPress: () -> Unit) {
  IconButton(
      onClick = upPress,
      modifier =
          Modifier.statusBarsPadding().padding(horizontal = 16.dp, vertical = 10.dp).size(36.dp)) {
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
            contentDescription = stringResource(R.string.label_back))
      }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleBar(
    upPress: () -> Unit,
    text: String,
    showBackBtn: Boolean = true,
    content: @Composable () -> Unit = {},
) {
  val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())

  TopAppBar(
      scrollBehavior = scrollBehavior,
      colors =
          TopAppBarDefaults.topAppBarColors(
              scrolledContainerColor = MaterialTheme.colorScheme.surface),
      title = { Text(text) },
      navigationIcon = {
        if (showBackBtn) {
          Up(upPress)
        }
      },
      actions = { content() },
  )
}
