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
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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
          initialValue = UserSettings.getDefaultInstance())

  fun onModeSelected(context: Context, mode: ModeOfOperation) {
    viewModelScope.launch {
      userSettingsRepository.setModeOfOperation(mode)
      MyWorkManager.rebindListeners(context, true)
    }
  }
}
