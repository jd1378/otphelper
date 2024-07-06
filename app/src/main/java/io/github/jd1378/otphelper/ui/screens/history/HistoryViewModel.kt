package io.github.jd1378.otphelper.ui.screens.history

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.repository.DetectedCodeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class HistoryViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val detectedCodeRepository: DetectedCodeRepository,
) : ViewModel() {
  val historyItems = detectedCodeRepository.get().cachedIn(viewModelScope)

  fun clearHistory() {
    showClearHistoryDialog.value = false
    viewModelScope.launch { detectedCodeRepository.deleteAll() }
  }

  val showClearHistoryDialog = MutableStateFlow(false)
}
