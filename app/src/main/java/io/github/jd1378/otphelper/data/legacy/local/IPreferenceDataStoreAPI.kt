package io.github.jd1378.otphelper.data.legacy.local

import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.flow.Flow

interface IPreferenceDataStoreAPI {
  fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T>

  suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T): T

  suspend fun <T> putPreference(key: Preferences.Key<T>, value: T)

  suspend fun <T> removePreference(key: Preferences.Key<T>)

  suspend fun <T> clearAllPreference()
}
