package io.github.jd1378.otphelper.data.legacy

import io.github.jd1378.otphelper.data.legacy.local.PreferenceDataStoreConstants
import io.github.jd1378.otphelper.data.legacy.local.PreferenceDataStoreHelper
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class OldIgnoredNotifSetRepository
@Inject
constructor(private val preferenceDataStoreHelper: PreferenceDataStoreHelper) {

  fun getIgnoredNotifSetStream(): Flow<Set<String>> {
    return preferenceDataStoreHelper.getPreference(
        PreferenceDataStoreConstants.IGNORED_NOTIF_SET, emptySet())
  }

  suspend fun getIgnoredNotifSet(): Set<String> {
    return preferenceDataStoreHelper.getFirstPreference(
        PreferenceDataStoreConstants.IGNORED_NOTIF_SET, emptySet())
  }

  suspend fun setIgnoredNotifSet(value: Set<String>) {
    return preferenceDataStoreHelper.putPreference(
        PreferenceDataStoreConstants.IGNORED_NOTIF_SET, value)
  }

  suspend fun addIgnoredNotif(ignoredNotif: String) {
    val ignores = this.getIgnoredNotifSet().toMutableSet()
    ignores.add(ignoredNotif)
    this.setIgnoredNotifSet(ignores)
  }

  suspend fun removeIgnoredNotif(ignoredNotif: String) {
    val ignores = this.getIgnoredNotifSet().toMutableSet()
    ignores.remove(ignoredNotif)
    this.setIgnoredNotifSet(ignores)
  }

  suspend fun hasIgnoredNotif(ignoredNotif: String?): Boolean {
    if (ignoredNotif == null) return false
    return this.getIgnoredNotifSet().contains(ignoredNotif)
  }
}
