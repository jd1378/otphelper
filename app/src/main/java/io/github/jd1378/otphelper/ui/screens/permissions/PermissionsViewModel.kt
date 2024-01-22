package io.github.jd1378.otphelper.ui.screens.permissions

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.NotificationListener
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.data.SettingsRepository
import io.github.jd1378.otphelper.utils.AutostartHelper
import io.github.jd1378.otphelper.utils.combine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PermissionsUiState(
    val hasNotifPerm: Boolean = false,
    val hasNotifListenerPerm: Boolean = false,
    val isIgnoringBatteryOptimizations: Boolean = false,
    val hasAutostartSettings: Boolean = false,
    val hasRestrictedSettings: Boolean = false,
    val showSkipWarning: Boolean = false,
    val hasDoneAllSteps: Boolean = false,
)

@HiltViewModel
class PermissionsViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository,
) : ViewModel() {

  private val _hasNotifPerm = MutableStateFlow(false)
  private val _hasNotifListenerPerm = MutableStateFlow(false)
  private val _isIgnoringBatteryOptimizations = MutableStateFlow(false)
  private val _hasAutoStartSettings = MutableStateFlow(false)
  private val _hasRestrictedSettings = MutableStateFlow(false)
  private val _showSkipWarning = MutableStateFlow(false)

  val uiState: StateFlow<PermissionsUiState> =
      combine(
              _hasNotifPerm,
              _hasNotifListenerPerm,
              _isIgnoringBatteryOptimizations,
              _hasAutoStartSettings,
              _hasRestrictedSettings,
              _showSkipWarning,
          ) {
              hasNotifPerm,
              hasNotifListenerPerm,
              isIgnoringBatteryOptimizations,
              hasAutostartSettings,
              hasRestrictedSettings,
              showSkipWarning ->
            PermissionsUiState(
                hasNotifPerm,
                hasNotifListenerPerm,
                isIgnoringBatteryOptimizations,
                hasAutostartSettings,
                hasRestrictedSettings,
                showSkipWarning,
                hasNotifPerm && hasNotifListenerPerm && isIgnoringBatteryOptimizations)
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5_000),
              initialValue = PermissionsUiState())

  fun updatePermissionsStatus(context: Context) {
    viewModelScope.launch {
      launch {
        _hasNotifPerm.update {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
          } else {
            true
          }
        }
      }
      launch {
        _hasNotifListenerPerm.update {
          NotificationManagerCompat.getEnabledListenerPackages(context)
              .contains(context.packageName)
        }
      }
      launch {
        _isIgnoringBatteryOptimizations.update {
          val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
          powerManager.isIgnoringBatteryOptimizations(context.packageName)
        }
      }
      launch { _hasAutoStartSettings.update { AutostartHelper.hasAutostartSettings(context) } }

      launch {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
          _hasRestrictedSettings.update { true }
        }
      }
    }
  }

  fun onSetupFinish(upPress: () -> Unit) {
    _showSkipWarning.update { false }
    viewModelScope.launch {
      settingsRepository.setIsSetupFinished(true)
      upPress()
    }
  }

  fun onSetupSkipPressed() {
    _showSkipWarning.update { true }
  }

  fun onOpenReadNotificationsPressed(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      // Go directly to the app's notification listener settings page
      val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_DETAIL_SETTINGS)
      intent.putExtra(
          Settings.EXTRA_NOTIFICATION_LISTENER_COMPONENT_NAME,
          ComponentName(
              context, NotificationListener::class.java,
          ).flattenToString(),
      )

      try {
        context.startActivity(intent)
      } catch (e: Exception) {
        // Not all phones had this action in Android 11, this is a fallback
        context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
      }

    } else {
      context.startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
    }
  }

  fun onOpenBatteryOptimizationsPressed(context: Context) {
    val intent = Intent().setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS)
    context.startActivity(intent)
  }

  fun onOpenAutostartPressed(context: Context) {
    AutostartHelper.openAutostartSettings(context)
  }

  fun onOpenAppSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            .addCategory(Intent.CATEGORY_DEFAULT)
            .setData(Uri.fromParts("package", context.packageName, null))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            .addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
            .addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
    try {
      context.startActivity(intent)
    } catch (e: Exception) {
      Toast.makeText(context, R.string.failed_to_open_app_settings, Toast.LENGTH_LONG).show()
    }
  }
}
