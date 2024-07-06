package io.github.jd1378.otphelper.ui.screens.home

import android.content.Context
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.UserSettings
import io.github.jd1378.otphelper.copy
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.NotificationHelper
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@Stable
@HiltViewModel
class HomeViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    userSettingsRepository: UserSettingsRepository
) : ViewModel() {

  val userSettings =
      userSettingsRepository.userSettings.stateIn(
          scope = viewModelScope,
          started = SharingStarted.Eagerly,
          initialValue = UserSettings.getDefaultInstance().copy { isSetupFinished = true })

  fun onSendTestNotifPressed(context: Context) {
    NotificationHelper.sendTestNotif(context)
  }
}
