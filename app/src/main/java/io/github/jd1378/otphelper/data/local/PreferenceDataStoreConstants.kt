package io.github.jd1378.otphelper.data.local

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey

object PreferenceDataStoreConstants {
  val IS_AUTO_COPY_ENABLED = booleanPreferencesKey("IS_AUTO_COPY_ENABLED")
  val IS_POST_NOTIF_ENABLED = booleanPreferencesKey("IS_POST_NOTIF_ENABLED")
  val IS_SETUP_FINISHED = booleanPreferencesKey("IS_SETUP_FINISHED")
  val IGNORED_NOTIF_SET = stringSetPreferencesKey("IGNORED_NOTIF_SET")
}
