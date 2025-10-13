package io.github.jd1378.otphelper.repository

import androidx.compose.runtime.Stable
import io.github.jd1378.otphelper.ModeOfOperation
import io.github.jd1378.otphelper.UserSettings
import kotlinx.coroutines.flow.Flow

@Stable
interface UserSettingsRepository {

  val userSettings: Flow<UserSettings>

  suspend fun fetchSettings(): UserSettings

  suspend fun saveSettings(userSettings: UserSettings)

  suspend fun setIsMigrationDone(value: Boolean)

  suspend fun setIsSetupFinished(value: Boolean)

  suspend fun setIsAutoCopyEnabled(value: Boolean)

  suspend fun setIsPostNotifEnabled(value: Boolean)

  suspend fun setIsShowCopyConfirmationEnabled(value: Boolean)

  suspend fun setIsHistoryDisabled(value: Boolean)

  suspend fun setShouldReplaceCodeInHistory(value: Boolean)

  suspend fun setSensitivePhrases(list: List<String>)

  suspend fun setIgnoredPhrases(list: List<String>)

  suspend fun setCleanupPhrases(list: List<String>)

  suspend fun setVersion(version: Int)

  suspend fun setIsAutoDismissEnabled(value: Boolean)

  suspend fun setIsAutoMarkAsReadEnabled(value: Boolean)

  suspend fun setIsShowToastEnabled(value: Boolean)

  suspend fun setIsCleanupPhrasesMigrated(value: Boolean)

  suspend fun setIsCopyAsNotSensitive(value: Boolean)

  suspend fun setModeOfOperation(value: ModeOfOperation)

  suspend fun setDetectionTestContent(value: String)

  suspend fun setIsBroadcastCodeEnabled(value: Boolean)

  suspend fun setBroadcastTargetPackageName(value: String)
}
