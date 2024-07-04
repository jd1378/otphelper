package io.github.jd1378.otphelper.repository

import androidx.compose.runtime.Stable
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

  suspend fun setIsCopiedToastEnabled(value: Boolean)
}
