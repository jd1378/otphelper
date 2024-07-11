package io.github.jd1378.otphelper.di

import android.util.Log
import androidx.compose.runtime.Stable
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.CodeExtractor
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Stable
class AutoUpdatingListenerUtils
@Inject
constructor(private val userSettingsRepository: UserSettingsRepository) {
  companion object {
    const val TAG = "AutoUpdatingCodeExtractor"
  }

  private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
    Log.d(TAG, exception.message ?: exception.toString())
  }

  private val scope = CoroutineScope(Dispatchers.IO + exceptionHandler)

  var codeExtractor: CodeExtractor? = null
    private set

  init {
    scope.launch {
      userSettingsRepository.userSettings.collect {
        codeExtractor = CodeExtractor(it.sensitivePhrasesList)
      }
    }
  }
}
