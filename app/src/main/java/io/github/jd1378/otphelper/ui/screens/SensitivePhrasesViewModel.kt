package io.github.jd1378.otphelper.ui.screens

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.di.AutoUpdatingListenerUtils
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.CodeExtractor
import io.github.jd1378.otphelper.utils.CodeExtractorDefaults
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
@HiltViewModel
class SensitivePhrasesViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userSettingsRepository: UserSettingsRepository,
    val autoUpdatingListenerUtils: AutoUpdatingListenerUtils,
) : ViewModel() {
  val showResetToDefaultDialog = MutableStateFlow(false)
  val showNewSensitivePhraseDialog = MutableStateFlow(false)
  val showClearListDialog = MutableStateFlow(false)

  val sensitivePhrases =
      userSettingsRepository.userSettings
          .map { it.sensitivePhrasesList.toPersistentList() }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = persistentListOf<String>(),
          )

  fun resetToDefault() {
    showResetToDefaultDialog.value = false
    viewModelScope.launch {
      userSettingsRepository.setSensitivePhrases(CodeExtractorDefaults.sensitivePhrases)
    }
  }

  fun clearList() {
    showClearListDialog.value = false
    viewModelScope.launch { userSettingsRepository.setSensitivePhrases(listOf()) }
  }

  fun addNewPhrase(it: String) {
    showNewSensitivePhraseDialog.value = false
    viewModelScope.launch {
      if (sensitivePhrases.value.indexOf(it) == -1) {
        val newList = sensitivePhrases.value.add(it)
        userSettingsRepository.setSensitivePhrases(newList)
      }
    }
  }

  fun deletePhrase(index: Int) {
    viewModelScope.launch {
      val newList = sensitivePhrases.value.removeAt(index)
      userSettingsRepository.setSensitivePhrases(newList)
    }
  }

  fun isSensitivePhraseParsable(str: String): Boolean {
    if (str.isBlank()) return false
    return try {
      CodeExtractor(listOf(str, "code")).getCode("Code: 123456") == "123456"
    } catch (e: Throwable) {
      false
    }
  }
}
