package io.github.jd1378.otphelper.ui.screens

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.data.local.entity.DetectedCode
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.di.AutoUpdatingCodeExtractor
import io.github.jd1378.otphelper.repository.DetectedCodeRepository
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.ui.navigation.NavArgs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class HistoryDetailViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val detectedCodeRepository: DetectedCodeRepository,
    private val ignoredNotifsRepository: IgnoredNotifsRepository,
    val autoUpdatingCodeExtractor: AutoUpdatingCodeExtractor
) : ViewModel() {

  private val historyId = savedStateHandle.getStateFlow<Long?>(NavArgs.HISTORY_ID, null)

  @OptIn(ExperimentalCoroutinesApi::class)
  val detectedCode =
      historyId
          .flatMapMerge { it?.let { detectedCodeRepository.getById(it) } ?: flow { emit(null) } }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = null,
          )

  @OptIn(ExperimentalCoroutinesApi::class)
  val isAppIgnored =
      detectedCode
          .flatMapMerge {
            it?.let { ignoredNotifsRepository.exists(it.packageName, IgnoredNotifType.APPLICATION) }
                ?: flow { emit(false) }
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = false,
          )

  @OptIn(ExperimentalCoroutinesApi::class)
  val isNotifTagIgnored =
      detectedCode
          .flatMapMerge {
            it?.let {
              ignoredNotifsRepository.exists(
                  it.packageName, IgnoredNotifType.NOTIFICATION_TAG, it.notificationTag ?: "")
            } ?: flow { emit(false) }
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = false,
          )

  @OptIn(ExperimentalCoroutinesApi::class)
  val isNotifIdIgnored =
      detectedCode
          .flatMapMerge {
            it?.let {
              ignoredNotifsRepository.exists(
                  it.packageName, IgnoredNotifType.NOTIFICATION_ID, it.notificationId)
            } ?: flow { emit(false) }
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = false,
          )

  fun toggleAppIgnore(detectedCode: DetectedCode) {
    viewModelScope.launch(Dispatchers.IO) {
      if (isAppIgnored.value) {
        ignoredNotifsRepository.deleteIgnored(
            detectedCode.packageName, IgnoredNotifType.APPLICATION, "")
      } else {
        ignoredNotifsRepository.setIgnored(
            detectedCode.packageName, IgnoredNotifType.APPLICATION, "")
      }
    }
  }

  fun toggleNotifIdIgnore(detectedCode: DetectedCode) {
    viewModelScope.launch(Dispatchers.IO) {
      if (isNotifIdIgnored.value) {
        ignoredNotifsRepository.deleteIgnored(
            detectedCode.packageName, IgnoredNotifType.NOTIFICATION_ID, detectedCode.notificationId)
      } else {
        ignoredNotifsRepository.setIgnored(
            detectedCode.packageName, IgnoredNotifType.NOTIFICATION_ID, detectedCode.notificationId)
      }
    }
  }

  fun toggleNotifTagIgnore(detectedCode: DetectedCode) {
    viewModelScope.launch(Dispatchers.IO) {
      if (isNotifTagIgnored.value) {
        ignoredNotifsRepository.deleteIgnored(
            detectedCode.packageName,
            IgnoredNotifType.NOTIFICATION_TAG,
            detectedCode.notificationTag)
      } else {
        ignoredNotifsRepository.setIgnored(
            detectedCode.packageName,
            IgnoredNotifType.NOTIFICATION_TAG,
            detectedCode.notificationTag)
      }
    }
  }
}
