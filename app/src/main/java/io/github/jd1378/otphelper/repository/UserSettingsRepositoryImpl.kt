package io.github.jd1378.otphelper.repository

import android.util.Log
import androidx.compose.runtime.Stable
import androidx.datastore.core.DataStore
import io.github.jd1378.otphelper.UserSettings
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first

@Singleton
@Stable
class UserSettingsRepositoryImpl
@Inject
constructor(
    private val userSettingsStore: DataStore<UserSettings>,
) : UserSettingsRepository {

  companion object {
    const val TAG = "UserSettingsRepositoryImpl"
  }

  override val userSettings: Flow<UserSettings>
    get() =
        userSettingsStore.data.catch { exception ->
          if (exception is IOException) {
            Log.e(TAG, "Error reading user settings.", exception)
            emit(UserSettings.getDefaultInstance())
          } else {
            throw exception
          }
        }

  override suspend fun fetchSettings() = userSettings.first()

  override suspend fun saveSettings(userSettings: UserSettings) {
    userSettingsStore.updateData { it.toBuilder().clear().mergeFrom(userSettings).build() }
  }

  override suspend fun setIsMigrationDone(value: Boolean) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().setIsMigrationDone(value).build()
    }
  }

  override suspend fun setIsSetupFinished(value: Boolean) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().setIsSetupFinished(value).build()
    }
  }

  override suspend fun setIsAutoCopyEnabled(value: Boolean) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().setIsAutoCopyEnabled(value).build()
    }
  }

  override suspend fun setIsPostNotifEnabled(value: Boolean) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().setIsPostNotifEnabled(value).build()
    }
  }

  override suspend fun setIsCopiedToastEnabled(value: Boolean) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().setIsCopiedToastEnabled(value).build()
    }
  }

  override suspend fun setIsHistoryDisabled(value: Boolean) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().setIsHistoryDisabled(value).build()
    }
  }

  override suspend fun setShouldReplaceCodeInHistory(value: Boolean) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().setShouldReplaceCodeInHistory(value).build()
    }
  }

  override suspend fun setSensitivePhrases(list: List<String>) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().clearSensitivePhrases().addAllSensitivePhrases(list).build()
    }
  }

  override suspend fun setIgnoredPhrases(list: List<String>) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().clearIgnoredPhrases().addAllIgnoredPhrases(list).build()
    }
  }

  override suspend fun setVersion(version: Int) {
    userSettingsStore.updateData { currentSettings ->
      currentSettings.toBuilder().setVersion(version).build()
    }
  }
}
