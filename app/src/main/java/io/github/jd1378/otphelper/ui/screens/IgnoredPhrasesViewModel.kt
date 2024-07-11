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
class IgnoredPhrasesViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userSettingsRepository: UserSettingsRepository,
    val autoUpdatingListenerUtils: AutoUpdatingListenerUtils,
) : ViewModel() {
  val showResetToDefaultDialog = MutableStateFlow(false)
  val showNewIgnoredPhraseDialog = MutableStateFlow(false)
  val showClearListDialog = MutableStateFlow(false)

  val ignoredPhrases =
      userSettingsRepository.userSettings
          .map { it.ignoredPhrasesList.toPersistentList() }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = persistentListOf<String>(),
          )

  fun resetToDefault() {
    showResetToDefaultDialog.value = false
    viewModelScope.launch {
      userSettingsRepository.setIgnoredPhrases(CodeExtractorDefaults.ignoredPhrases)
    }
  }

  fun clearList() {
    showClearListDialog.value = false
    viewModelScope.launch { userSettingsRepository.setIgnoredPhrases(listOf()) }
  }

  fun addNewPhrase(it: String) {
    showNewIgnoredPhraseDialog.value = false
    viewModelScope.launch {
      if (ignoredPhrases.value.indexOf(it) == -1) {
        val newList = ignoredPhrases.value.add(it)
        userSettingsRepository.setIgnoredPhrases(newList)
      }
    }
  }

  fun deletePhrase(index: Int) {
    viewModelScope.launch {
      val newList = ignoredPhrases.value.removeAt(index)
      userSettingsRepository.setIgnoredPhrases(newList)
    }
  }

  fun isIgnoredPhraseParsable(str: String): Boolean {
    if (str.isBlank()) return false
    return try {
      CodeExtractor(listOf("code"), listOf(str, "a_b_c_d_e")).shouldIgnore("a_b_c_d_e")
    } catch (e: Throwable) {
      false
    }
  }
}
