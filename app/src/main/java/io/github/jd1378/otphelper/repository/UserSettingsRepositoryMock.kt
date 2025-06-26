package io.github.jd1378.otphelper.repository

import io.github.jd1378.otphelper.ModeOfOperation
import io.github.jd1378.otphelper.UserSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class UserSettingsRepositoryMock : UserSettingsRepository {
  override val userSettings: Flow<UserSettings>
    get() = flow { UserSettings.getDefaultInstance() }

  override suspend fun fetchSettings(): UserSettings {
    TODO("Not yet implemented")
  }

  override suspend fun saveSettings(userSettings: UserSettings) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsMigrationDone(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsSetupFinished(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsAutoCopyEnabled(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsPostNotifEnabled(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsShowCopyConfirmationEnabled(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsHistoryDisabled(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setShouldReplaceCodeInHistory(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setSensitivePhrases(list: List<String>) {
    TODO("Not yet implemented")
  }

  override suspend fun setIgnoredPhrases(list: List<String>) {
    TODO("Not yet implemented")
  }

  override suspend fun setCleanupPhrases(list: List<String>) {
    TODO("Not yet implemented")
  }

  override suspend fun setVersion(version: Int) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsAutoDismissEnabled(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsAutoMarkAsReadEnabled(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsShowToastEnabled(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setIsCleanupPhrasesMigrated(value: Boolean) {
    TODO("Not yet implemented")
  }

  override suspend fun setModeOfOperation(value: ModeOfOperation) {
    TODO("Not yet implemented")
  }
}
