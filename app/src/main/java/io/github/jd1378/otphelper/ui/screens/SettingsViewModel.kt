package io.github.jd1378.otphelper.ui.screens

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
    private val userSettingsRepository: UserSettingsRepository,
) : ViewModel() {

  val userSettings =
      userSettingsRepository.userSettings.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = UserSettings.getDefaultInstance(),
      )

  fun onAutoCopyToggle() {
    val newValue = !userSettings.value.isAutoCopyEnabled
    viewModelScope.launch {
      userSettingsRepository.setIsAutoCopyEnabled(newValue)
      if (
          !newValue &&
              !userSettings.value.isPostNotifEnabled &&
              !userSettings.value.isShowToastEnabled
      ) {
        userSettingsRepository.setIsPostNotifEnabled(true)
      }
    }
  }

  fun onPostNotifToggle() {
    viewModelScope.launch {
      if (userSettings.value.isPostNotifEnabled && !userSettings.value.isShowToastEnabled) {
        userSettingsRepository.setIsAutoCopyEnabled(true)
      }
      userSettingsRepository.setIsPostNotifEnabled(!userSettings.value.isPostNotifEnabled)
    }
  }

  fun onShowCopyConfirmationToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsShowCopyConfirmationEnabled(
          !userSettings.value.isShowCopyConfirmationEnabled
      )
    }
  }

  fun onShowToastToggle() {
    viewModelScope.launch {
      if (userSettings.value.isShowToastEnabled && !userSettings.value.isPostNotifEnabled) {
        userSettingsRepository.setIsAutoCopyEnabled(true)
      }
      userSettingsRepository.setIsShowToastEnabled(!userSettings.value.isShowToastEnabled)
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
          !userSettings.value.shouldReplaceCodeInHistory
      )
    }
  }

  fun onSendTestNotifPressed(context: Context) {
    NotificationHelper.sendTestNotif(context)
  }

  fun onAutoDismissToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsAutoDismissEnabled(!userSettings.value.isAutoDismissEnabled)
    }
  }

  fun onAutoMarkAsReadToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsAutoMarkAsReadEnabled(!userSettings.value.isAutoMarkAsReadEnabled)
    }
  }

  fun onIsCopyAsNotSensitiveToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsCopyAsNotSensitive(
          !userSettings.value.isCopyAsNotSensitiveEnabled
      )
    }
  }

  fun onBroadcastCodeToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsBroadcastCodeEnabled(!userSettings.value.isBroadcastCodeEnabled)
    }
  }

  fun setBroadcastTargetPackageName(str: String) {
    viewModelScope.launch { userSettingsRepository.setBroadcastTargetPackageName(str) }
  }
}
