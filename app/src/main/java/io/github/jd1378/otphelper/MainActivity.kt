package io.github.jd1378.otphelper

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import io.github.jd1378.otphelper.utils.ActivityHelper

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
  companion object {
    var observer: MyLifecycleObserver? = null
    const val scale = 1.15f
  }

  override fun attachBaseContext(newBase: Context) {
    super.attachBaseContext(ActivityHelper.adjustFontSize(newBase, scale))
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    ActivityHelper.adjustFontSize(this, scale)
    observer = MyLifecycleObserver(activityResultRegistry)
    lifecycle.addObserver(observer!!)

    setContent { OtpHelperApp() }
  }

  override fun onDestroy() {
    observer = null
    super.onDestroy()
  }
}
