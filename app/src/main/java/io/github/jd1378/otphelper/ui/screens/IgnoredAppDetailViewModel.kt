package io.github.jd1378.otphelper.ui.screens

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.ui.navigation.NavArgs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class IgnoredAppDetailViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val ignoredNotifsRepository: IgnoredNotifsRepository
) : ViewModel() {

  val packageName = savedStateHandle.getStateFlow<String?>(NavArgs.PACKAGE_NAME, null)

  @OptIn(ExperimentalCoroutinesApi::class)
  val ignoredItems =
      packageName
          .flatMapMerge {
            it?.let { ignoredNotifsRepository.getByPackageName(it) }
                ?: flow { emit(PagingData.empty()) }
          }
          .cachedIn(viewModelScope)

  fun removeIgnoredNotif(ignoredNotif: IgnoredNotif) {
    viewModelScope.launch { ignoredNotifsRepository.deleteIgnored(ignoredNotif) }
  }
}
