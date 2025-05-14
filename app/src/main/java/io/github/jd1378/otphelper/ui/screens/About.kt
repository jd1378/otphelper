package io.github.jd1378.otphelper.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.jd1378.otphelper.BuildConfig
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.ui.components.LinkText
import io.github.jd1378.otphelper.ui.components.LinkTextData
import io.github.jd1378.otphelper.ui.components.TitleBar
import kotlinx.collections.immutable.persistentListOf

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
            Image(
                painter = painterResource(R.drawable.logo_round),
                contentDescription = null,
                modifier = Modifier.size(128.dp).shadow(3.dp, shape = CircleShape),
            )

            Text(
                stringResource(R.string.app_name),
                modifier = Modifier.padding(PaddingValues(10.dp)),
                fontSize = 28.sp)

            val labelStyle =
                LocalTextStyle.current.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium)

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                  Text(text = stringResource(R.string.label_version), style = labelStyle)
                  Text(BuildConfig.VERSION_NAME, fontSize = 20.sp)
                }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center) {
                  Text(text = stringResource(R.string.label_license), style = labelStyle)
                  Text(stringResource(R.string.license_type), fontSize = 20.sp)
                }

            val uriHandler = LocalUriHandler.current

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
              Text(text = stringResource(R.string.label_source_code_link), style = labelStyle)

              Row(verticalAlignment = Alignment.CenterVertically) {
                LinkText(
                    linkTextData =
                        persistentListOf(
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

            Row(verticalAlignment = Alignment.CenterVertically) {
              LinkText(
                  linkTextData =
                      persistentListOf(
                          LinkTextData(
                              text = stringResource(R.string.label_privacy_policy),
                              tag = "label_privacy_policy",
                              annotation = "https://jd1378.github.io/otphelper/privacy-policy",
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
