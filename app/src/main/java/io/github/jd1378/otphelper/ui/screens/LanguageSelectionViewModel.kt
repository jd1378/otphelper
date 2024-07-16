package io.github.jd1378.otphelper.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.R
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Immutable data class LocaleOption(val code: String, val label: Int, val tag: String = "")

val localeOptions =
    persistentListOf(
        LocaleOption("default", R.string.system_default, "default"),
        LocaleOption("ar", R.string.ar, "العربية,arabic"),
        LocaleOption("bn-BD", R.string.bn_BD, "bangla,bangladesh"),
        LocaleOption("en", R.string.en, "english"),
        LocaleOption("es", R.string.es, "spanish,español"),
        LocaleOption("fa", R.string.fa, "farsi,persian,فارسی"),
        LocaleOption("fr", R.string.fr, "français,french"),
        LocaleOption("de", R.string.de, "deutsch,german"),
        LocaleOption("hi", R.string.hi, "हिंदी,hindi"),
        LocaleOption("it", R.string.it, "italiano"),
        LocaleOption("ru", R.string.ru, "Русский,russian"),
        LocaleOption("tr", R.string.tr, "turkish,türkçe"),
        LocaleOption("vi", R.string.vi, "Tiếng Việt,vietnamese"),
        LocaleOption("zh-Hans", R.string.zh_Hans, "简体中文,simplified,chinese"),
        LocaleOption("zh-Hant", R.string.zh_Hant, "繁體中文,traditional,chinese"),
        LocaleOption("uk", R.string.uk, "українська,ukrainian"),
        LocaleOption("pt", R.string.pt, "Português,Portuguese"),
        LocaleOption("pt-BR", R.string.pt_rBR, "Português,Portuguese,brasil,brazil"),
        LocaleOption("jp", R.string.jp, "Japanese,nihongo,日本語"),
        LocaleOption("ko", R.string.ko, "korean,한국어"),
    )

@Immutable
data class LanguageSelectionUiState(
    val locales: List<LocaleOption> = localeOptions,
    val searchTerm: String = "",
)

@Stable
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
