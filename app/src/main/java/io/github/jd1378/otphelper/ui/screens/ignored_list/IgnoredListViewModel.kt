package io.github.jd1378.otphelper.ui.screens.ignored_list

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.data.IgnoredNotifSetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class IgnoredListUiState(
    val ignoredNotifs: List<String> = emptyList(),
    var deleting: Boolean = false
)

@HiltViewModel
class IgnoredListViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ignoredNotifSetRepository: IgnoredNotifSetRepository
) : ViewModel() {

  private val _ignoredNotifs = ignoredNotifSetRepository.getIgnoredNotifSetStream()
  private val _isDeleting = MutableStateFlow(false)

  val uiState: StateFlow<IgnoredListUiState> =
      combine(_ignoredNotifs, _isDeleting) { ignoredNotifs, isDeleting ->
            IgnoredListUiState(ignoredNotifs.toList(), isDeleting)
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5_000),
              initialValue = IgnoredListUiState())

  fun removeIgnoredNotif(ignoredNotif: String) {
    if (_isDeleting.value) return
    viewModelScope.launch {
      _isDeleting.update { true }
      ignoredNotifSetRepository.removeIgnoredNotif(ignoredNotif)
      _isDeleting.update { false }
    }
  }
}
