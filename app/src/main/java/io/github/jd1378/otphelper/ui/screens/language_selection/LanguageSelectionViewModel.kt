package io.github.jd1378.otphelper.ui.screens.language_selection

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.R
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

data class LocaleOption(val code: String, val label: Int, val tag: String = "")

val localeOptions =
    listOf(
        LocaleOption("default", R.string.system_default, "default"),
        LocaleOption("de", R.string.de, "deutsch"),
        LocaleOption("en", R.string.en, "english"),
        LocaleOption("es", R.string.es, "spanish"),
        LocaleOption("fa", R.string.fa, "farsi,persian,?????"))

data class LanguageSelectionUiState(
    val locales: List<LocaleOption> = localeOptions,
    val searchTerm: String = "",
)

@HiltViewModel
class LanguageSelectionViewModel
@Inject
constructor(private val savedStateHandle: SavedStateHandle) : ViewModel() {

  private val _filteredLocaleOptions = MutableStateFlow(localeOptions)
  private val _searchTerm = MutableStateFlow("")

  val uiState: StateFlow<LanguageSelectionUiState> =
      combine(_filteredLocaleOptions, _searchTerm) { filteredLocaleOptions, searchTerm,
            ->
            LanguageSelectionUiState(
                filteredLocaleOptions.filter {
                  if (searchTerm.isNotEmpty()) it.tag.contains(searchTerm, ignoreCase = true)
                  else true
                },
                searchTerm)
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(5_000),
              initialValue = LanguageSelectionUiState())

  fun setSearchTerm(newTerm: String) {
    _searchTerm.update { newTerm }
  }

  fun selectLocale(locale: LocaleOption) {
    if (locale.code == "default") {
      AppCompatDelegate.setApplicationLocales(LocaleListCompat.getEmptyLocaleList())
    } else {
      AppCompatDelegate.setApplicationLocales(
          LocaleListCompat.forLanguageTags(locale.code),
      )
    }
  }
}
