package io.github.jd1378.otphelper.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AppLabel(packageName: String, modifier: Modifier = Modifier) {
  val context = LocalContext.current
  var label: String? = null

  try {
    val packageManager = context.packageManager
    val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
    label = packageManager.getApplicationLabel(applicationInfo).toString()
  } catch (e: Exception) {
    e.printStackTrace()
  }

  label?.let { Text(label, modifier = modifier) }
}

@Preview(showBackground = true)
@Composable
fun AppLabelPreview() {
  AppLabel(packageName = "io.github.jd1378.otphelper")
}
