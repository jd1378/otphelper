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
import javax.inject.Inject
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
@HiltViewModel
class CleanupPhrasesViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userSettingsRepository: UserSettingsRepository,
    val autoUpdatingListenerUtils: AutoUpdatingListenerUtils,
) : ViewModel() {
  val showResetToDefaultDialog = MutableStateFlow(false)
  val showNewCleanupPhraseDialog = MutableStateFlow(false)
  val showClearListDialog = MutableStateFlow(false)

  val cleanupPhrases =
      userSettingsRepository.userSettings
          .map { it.cleanupPhrasesList.toPersistentList() }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5000),
              initialValue = persistentListOf<String>(),
          )

  fun resetToDefault() {
    showResetToDefaultDialog.value = false
    viewModelScope.launch {
      userSettingsRepository.setCleanupPhrases(CodeExtractorDefaults.cleanupPhrases)
    }
  }

  fun clearList() {
    showClearListDialog.value = false
    viewModelScope.launch { userSettingsRepository.setCleanupPhrases(listOf()) }
  }

  fun addNewPhrase(it: String) {
    showNewCleanupPhraseDialog.value = false
    viewModelScope.launch {
      if (cleanupPhrases.value.indexOf(it) == -1) {
        val newList = cleanupPhrases.value.add(it)
        userSettingsRepository.setCleanupPhrases(newList)
      }
    }
  }

  fun deletePhrase(index: Int) {
    viewModelScope.launch {
      val newList = cleanupPhrases.value.removeAt(index)
      userSettingsRepository.setCleanupPhrases(newList)
    }
  }

  fun isCleanupPhraseParsable(str: String): Boolean {
    if (str.isBlank()) return false
    return try {
      CodeExtractor(listOf("code"), listOf("foo"), listOf(str, "a_b_c_d_e")).cleanup("bar")
      return true
    } catch (e: Throwable) {
      false
    }
  }
}
