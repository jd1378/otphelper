package io.github.jd1378.otphelper.ui.screens.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.UserSettings
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

  val userSettings =
      userSettingsRepository.userSettings.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = UserSettings.getDefaultInstance())

  fun onAutoCopyToggle() {
    val newValue = !userSettings.value.isAutoCopyEnabled
    viewModelScope.launch {
      userSettingsRepository.setIsAutoCopyEnabled(newValue)
      if (!newValue && !userSettings.value.isPostNotifEnabled) {
        userSettingsRepository.setIsPostNotifEnabled(true)
      }
    }
  }

  fun onPostNotifToggle() {
    val currentValue = userSettings.value.isPostNotifEnabled
    viewModelScope.launch {
      if (currentValue) {
        userSettingsRepository.setIsAutoCopyEnabled(true)
      }
      userSettingsRepository.setIsPostNotifEnabled(!currentValue)
    }
  }
}
