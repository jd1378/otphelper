package io.github.jd1378.otphelper.ui.screens.permissions

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
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.UserSettings
import io.github.jd1378.otphelper.copy
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.AutostartHelper
import io.github.jd1378.otphelper.utils.SettingsHelper
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
    val userSettings: UserSettings =
        UserSettings.getDefaultInstance().copy { isSetupFinished = true },
)

@HiltViewModel
class PermissionsViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userSettingsRepository: UserSettingsRepository,
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
              userSettingsRepository.userSettings,
          ) {
              hasNotifPerm,
              hasNotifListenerPerm,
              isIgnoringBatteryOptimizations,
              hasAutostartSettings,
              hasRestrictedSettings,
              showSkipWarning,
              userSettings ->
            PermissionsUiState(
                hasNotifPerm,
                hasNotifListenerPerm,
                isIgnoringBatteryOptimizations,
                hasAutostartSettings,
                hasRestrictedSettings,
                showSkipWarning,
                hasNotifPerm && hasNotifListenerPerm && isIgnoringBatteryOptimizations,
                userSettings,
            )
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

  fun onSetupFinish() {
    _showSkipWarning.update { false }
    viewModelScope.launch { userSettingsRepository.setIsSetupFinished(true) }
  }

  fun onSetupSkipPressed() {
    _showSkipWarning.update { true }
  }

  fun onOpenReadNotificationsPressed(context: Context) {
    SettingsHelper.openNotificationListenerSettings(context)
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
