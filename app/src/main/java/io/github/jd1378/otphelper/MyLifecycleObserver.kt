package io.github.jd1378.otphelper

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MyLifecycleObserver(private val registry: ActivityResultRegistry) : DefaultLifecycleObserver {
  private lateinit var requestPermission: ActivityResultLauncher<String>

  // define a permission request result callback(optional)
  private var callback: ((Boolean) -> Unit)? = null

  override fun onCreate(owner: LifecycleOwner) {
    requestPermission =
        registry.register("key", owner, ActivityResultContracts.RequestPermission()) { granted ->
          // Handle the returned granted
          callback?.invoke(granted)
        }
  }

  fun requestPermission(permission: String) {
    requestPermission(permission, null)
  }

  fun requestPermission(permission: String, callback: ((Boolean) -> Unit)?) {
    requestPermission.launch(permission)
    if (callback !== null) {
      this.callback = callback
    }
  }
}
