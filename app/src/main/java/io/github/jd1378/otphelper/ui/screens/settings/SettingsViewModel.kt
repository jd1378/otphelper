package io.github.jd1378.otphelper.ui.screens.settings

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.MyWorkManager.disableHistoryCleanup
import io.github.jd1378.otphelper.MyWorkManager.enableHistoryCleanup
import io.github.jd1378.otphelper.UserSettings
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
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

  fun onCopiedToastToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsCopiedToastEnabled(!userSettings.value.isCopiedToastEnabled)
    }
  }

  fun onHistoryToggle(context: Context) {
    viewModelScope.launch {
      val newIsHistoryDisabled = !userSettings.value.isHistoryDisabled
      userSettingsRepository.setIsHistoryDisabled(newIsHistoryDisabled)

      if (newIsHistoryDisabled) {
        disableHistoryCleanup(context)
      } else {
        enableHistoryCleanup(context)
      }
    }
  }

  fun onShouldReplaceCodeInHistoryToggle() {
    viewModelScope.launch {
      userSettingsRepository.setShouldReplaceCodeInHistory(
          !userSettings.value.shouldReplaceCodeInHistory)
    }
  }

  fun onSendTestNotifPressed(context: Context) {
    NotificationHelper.sendTestNotif(context)
  }
}
