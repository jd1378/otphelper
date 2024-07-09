package io.github.jd1378.otphelper.ui.components

import android.content.Context
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview

/** returns the package name if app label is not found. */
fun getAppLabel(context: Context, packageName: String?): String {
  if (packageName.isNullOrEmpty()) return ""
  try {
    val packageManager = context.packageManager
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    return packageManager.getApplicationLabel(applicationInfo).toString()
  } catch (e: Exception) {
    return packageName
  }
}

@Composable
fun AppLabel(
  modifier: Modifier = Modifier,
  packageName: String,
  textStyle: TextStyle = LocalTextStyle.current
) {
  val context = LocalContext.current
  var label: String? = getAppLabel(context, packageName)

  label?.let { Text(label, style = textStyle, modifier = modifier) }
}

@Preview(showBackground = true)
@Composable
fun AppLabelPreview() {
  AppLabel(packageName = "io.github.jd1378.otphelper")
}
