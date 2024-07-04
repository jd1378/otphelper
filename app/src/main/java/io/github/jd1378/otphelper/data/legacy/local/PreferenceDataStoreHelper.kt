package io.github.jd1378.otphelper.data.legacy.local

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "PreferenceDataStore")

class PreferenceDataStoreHelper @Inject constructor(@ApplicationContext context: Context) :
    IPreferenceDataStoreAPI {

  // dataSource access the DataStore file and does the manipulation based on our requirements.
  private val dataSource = context.dataStore

  /* This returns us a flow of data from DataStore.
  Basically as soon we update the value in Datastore,
  the values returned by it also changes. */
  override fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): Flow<T> =
      dataSource.data
          .catch { exception ->
            if (exception is IOException) {
              emit(emptyPreferences())
            } else {
              throw exception
            }
          }
          .map { preferences ->
            val result = preferences[key] ?: defaultValue
            result
          }

  /* This returns the last saved value of the key. If we change the value,
  it wont effect the values produced by this function */
  override suspend fun <T> getFirstPreference(key: Preferences.Key<T>, defaultValue: T): T =
      dataSource.data.first()[key] ?: defaultValue

  // This Sets the value based on the value passed in value parameter.
  override suspend fun <T> putPreference(key: Preferences.Key<T>, value: T) {
    dataSource.edit { preferences -> preferences[key] = value }
  }

  // This Function removes the Key Value pair from the datastore, hereby removing it completely.
  override suspend fun <T> removePreference(key: Preferences.Key<T>) {
    dataSource.edit { preferences -> preferences.remove(key) }
  }

  // This function clears the entire Preference Datastore.
  override suspend fun <T> clearAllPreference() {
    dataSource.edit { preferences -> preferences.clear() }
  }
}
