package io.github.jd1378.otphelper.ui.screens.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import io.github.jd1378.otphelper.BuildConfig
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.LinkText
import io.github.jd1378.otphelper.ui.components.LinkTextData
import io.github.jd1378.otphelper.ui.components.TitleBar
import io.github.jd1378.otphelper.ui.navigation.MainDestinations

fun NavGraphBuilder.addAboutGraph(upPress: () -> Unit) {
  composable(
      MainDestinations.ABOUT_ROUTE,
  ) {
    About(upPress)
  }
}

@Composable
fun About(upPress: () -> Unit) {
  Scaffold(
      modifier = Modifier.fillMaxSize(),
      topBar = {
        TitleBar(
            upPress = upPress,
            text = stringResource(R.string.about),
        )
      }) { padding ->
        Column(
            Modifier.padding(padding).fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          Column(
              Modifier.width(IntrinsicSize.Max),
              verticalArrangement = Arrangement.spacedBy(20.dp),
              horizontalAlignment = Alignment.CenterHorizontally,
          ) {
            Text(
                stringResource(R.string.app_name),
                modifier = Modifier.padding(PaddingValues(10.dp)),
                fontSize = 26.sp)

            Image(
                painter = painterResource(R.drawable.logo_round),
                contentDescription = null,
                modifier = Modifier.size(128.dp).shadow(3.dp, shape = CircleShape),
            )

            val labelStyle =
                LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                  Text(
                      text = stringResource(R.string.label_version),
                      fontSize = 16.sp,
                      style = labelStyle)
                  Text(BuildConfig.VERSION_NAME, fontSize = 20.sp)
                }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                  Text(
                      text = stringResource(R.string.label_license),
                      fontSize = 16.sp,
                      style = labelStyle)
                  Text(stringResource(R.string.license_type), fontSize = 20.sp)
                }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                  Text(
                      text = stringResource(R.string.label_source_code_link),
                      fontSize = 16.sp,
                      style = labelStyle)

                  val uriHandler = LocalUriHandler.current

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    LinkText(
                        linkTextData =
                            listOf(
                                LinkTextData(
                                    text = stringResource(R.string.github),
                                    tag = "github_homepage",
                                    annotation = "https://github.com/jd1378/otphelper",
                                    onClick = { uriHandler.openUri(it.item) },
                                ),
                            ),
                        fontSize = 21.sp,
                    )
                  }
                }
          }
        }
      }
}
