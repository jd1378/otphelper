package io.github.jd1378.otphelper.ui.screens.ignored_app_list

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class IgnoredAppListViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ignoredNotifsRepository: IgnoredNotifsRepository
) : ViewModel() {

  val ignoredApps = ignoredNotifsRepository.getGroupedByPackageName().cachedIn(viewModelScope)
  var isDeleting by mutableStateOf(false)
    private set

  fun removeIgnoredNotif(ignoredNotif: IgnoredNotif) {
    if (isDeleting) return
    viewModelScope.launch {
      isDeleting = true
      ignoredNotifsRepository.deleteIgnored(ignoredNotif)
      isDeleting = false
    }
  }
}
