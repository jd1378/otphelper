package io.github.jd1378.otphelper.ui.screens.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.data.SettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsUiState(
    val isAutoCopyEnabled: Boolean = false,
    val isPostNotifEnabled: Boolean = true,
)

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val settingsRepository: SettingsRepository
) : ViewModel() {

  val uiState: StateFlow<SettingsUiState> =
      combine(
              settingsRepository.getIsAutoCopyEnabledStream(),
              settingsRepository.getIsPostNotifEnabledStream()) {
                  isAutoCopyEnabled,
                  isPostNotifEnabled ->
                SettingsUiState(isAutoCopyEnabled, isPostNotifEnabled)
              }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5_000),
              initialValue = SettingsUiState())

  fun onAutoCopyToggle() {
    val newValue = !uiState.value.isAutoCopyEnabled
    viewModelScope.launch {
      settingsRepository.setIsAutoCopyEnabled(newValue)
      if (!newValue && !uiState.value.isPostNotifEnabled) {
        settingsRepository.setIsPostNotifEnabled(true)
      }
    }
  }

  fun onPostNotifToggle() {
    val currentValue = uiState.value.isPostNotifEnabled
    viewModelScope.launch {
      if (currentValue) {
        settingsRepository.setIsAutoCopyEnabled(true)
      }
      settingsRepository.setIsPostNotifEnabled(!currentValue)
    }
  }
}
