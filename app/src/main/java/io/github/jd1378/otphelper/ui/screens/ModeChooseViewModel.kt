package io.github.jd1378.otphelper.ui.screens

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.ModeOfOperation
import io.github.jd1378.otphelper.MyWorkManager
import io.github.jd1378.otphelper.UserSettings
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class ModeChooseViewModel
@Inject
constructor(
  private val savedStateHandle: SavedStateHandle,
  private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {

  val userSettings =
      userSettingsRepository.userSettings.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = UserSettings.getDefaultInstance(),
      )

  private val _showPermissionsReminder = MutableStateFlow(false)
  val showPermissionsReminder = _showPermissionsReminder.asStateFlow()

  fun setShowPermissionsReminder(value: Boolean) {
    _showPermissionsReminder.value = value
  }

  fun onModeSelected(context: Context, mode: ModeOfOperation) {
    viewModelScope.launch {
      if (userSettings.value.modeOfOperation != mode) {
        setShowPermissionsReminder(true)
      }
      userSettingsRepository.setModeOfOperation(mode)
      MyWorkManager.rebindListeners(context, true)
    }
  }
}
