package io.github.jd1378.otphelper.ui.screens

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.di.AutoUpdatingListenerUtils
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class DetectionTestViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userSettingsRepository: UserSettingsRepository,
    val autoUpdatingListenerUtils: AutoUpdatingListenerUtils,
) : ViewModel() {

  suspend fun getSavedDetectionTestContent(): String {
    return userSettingsRepository.fetchSettings().detectionTestContent
  }

  fun saveDetectionTestContent(value: String) {
    viewModelScope.launch { userSettingsRepository.setDetectionTestContent(value) }
  }
}
