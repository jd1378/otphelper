package io.github.jd1378.otphelper.ui.screens

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.github.jd1378.otphelper.BuildConfig
import io.github.jd1378.otphelper.MyWorkManager.disableHistoryCleanup
import io.github.jd1378.otphelper.MyWorkManager.enableHistoryCleanup
import io.github.jd1378.otphelper.R
import io.github.jd1378.otphelper.UserSettings
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.repository.IgnoredNotifsRepository
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.AppLogger
import io.github.jd1378.otphelper.utils.NotificationHelper
import io.github.jd1378.otphelper.utils.showToast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.Date
import javax.inject.Inject

@Stable
@HiltViewModel
class SettingsViewModel
@Inject
constructor(
    private val savedStateHandle: SavedStateHandle,
    private val userSettingsRepository: UserSettingsRepository,
    private val ignoredNotifsRepository: IgnoredNotifsRepository? = null,
) : ViewModel() {

  val userSettings =
      userSettingsRepository.userSettings.stateIn(
          scope = viewModelScope,
          started = SharingStarted.WhileSubscribed(5_000),
          initialValue = UserSettings.getDefaultInstance(),
      )

  fun onAutoCopyToggle() {
    val newValue = !userSettings.value.isAutoCopyEnabled
    viewModelScope.launch {
      userSettingsRepository.setIsAutoCopyEnabled(newValue)
      if (
          !newValue &&
              !userSettings.value.isPostNotifEnabled &&
              !userSettings.value.isShowToastEnabled
      ) {
        userSettingsRepository.setIsPostNotifEnabled(true)
      }
    }
  }

  fun onPostNotifToggle() {
    viewModelScope.launch {
      if (userSettings.value.isPostNotifEnabled && !userSettings.value.isShowToastEnabled) {
        userSettingsRepository.setIsAutoCopyEnabled(true)
      }
      userSettingsRepository.setIsPostNotifEnabled(!userSettings.value.isPostNotifEnabled)
    }
  }

  fun onShowCopyConfirmationToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsShowCopyConfirmationEnabled(
          !userSettings.value.isShowCopyConfirmationEnabled
      )
    }
  }

  fun onShowToastToggle() {
    viewModelScope.launch {
      if (userSettings.value.isShowToastEnabled && !userSettings.value.isPostNotifEnabled) {
        userSettingsRepository.setIsAutoCopyEnabled(true)
      }
      userSettingsRepository.setIsShowToastEnabled(!userSettings.value.isShowToastEnabled)
    }
  }

  fun onHistoryToggle(context: Context) {
    viewModelScope.launch {
      val newIsHistoryDisabled = !userSettings.value.isHistoryDisabled
      userSettingsRepository.setIsHistoryDisabled(newIsHistoryDisabled)

      if (newIsHistoryDisabled) {
        disableHistoryCleanup(context)
      } else {
        enableHistoryCleanup(context)
      }
    }
  }

  fun onShouldReplaceCodeInHistoryToggle() {
    viewModelScope.launch {
      userSettingsRepository.setShouldReplaceCodeInHistory(
          !userSettings.value.shouldReplaceCodeInHistory
      )
    }
  }

  fun onSendTestNotifPressed(context: Context) {
    NotificationHelper.sendTestNotif(context)
  }

  fun onAutoDismissToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsAutoDismissEnabled(!userSettings.value.isAutoDismissEnabled)
    }
  }

  fun onAutoMarkAsReadToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsAutoMarkAsReadEnabled(!userSettings.value.isAutoMarkAsReadEnabled)
    }
  }

  fun onIsCopyAsNotSensitiveToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsCopyAsNotSensitive(
          !userSettings.value.isCopyAsNotSensitiveEnabled
      )
    }
  }

  fun onBroadcastCodeToggle() {
    viewModelScope.launch {
      userSettingsRepository.setIsBroadcastCodeEnabled(!userSettings.value.isBroadcastCodeEnabled)
    }
  }

  suspend fun getBroadcastTargetPackageName(): String {
    return userSettingsRepository.fetchSettings().broadcastTargetPackageName
  }

  fun setBroadcastTargetPackageName(str: String) {
    viewModelScope.launch { userSettingsRepository.setBroadcastTargetPackageName(str) }
  }

  fun exportConfiguration(context: Context, uri: Uri) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val settings = userSettingsRepository.fetchSettings()
        val ignored = ignoredNotifsRepository?.getAll() ?: emptyList()

        val rootJson = JSONObject().apply {
          put("schemaVersion", 1)
          put("appVersion", BuildConfig.VERSION_NAME)
          put("exportedAt", System.currentTimeMillis())

          val settingsObj = JSONObject().apply {
            put("isAutoCopyEnabled", settings.isAutoCopyEnabled)
            put("isPostNotifEnabled", settings.isPostNotifEnabled)
            put("isShowCopyConfirmationEnabled", settings.isShowCopyConfirmationEnabled)
            put("isHistoryDisabled", settings.isHistoryDisabled)
            put("shouldReplaceCodeInHistory", settings.shouldReplaceCodeInHistory)
            put("isAutoDismissEnabled", settings.isAutoDismissEnabled)
            put("isAutoMarkAsReadEnabled", settings.isAutoMarkAsReadEnabled)
            put("isShowToastEnabled", settings.isShowToastEnabled)
            put("isCopyAsNotSensitiveEnabled", settings.isCopyAsNotSensitiveEnabled)
            put("isBroadcastCodeEnabled", settings.isBroadcastCodeEnabled)
            put("broadcastTargetPackageName", settings.broadcastTargetPackageName)
            put("modeOfOperation", settings.modeOfOperation.name)
            put("detectionTestContent", settings.detectionTestContent)
            put("sensitivePhrases", JSONArray(settings.sensitivePhrasesList))
            put("ignoredPhrases", JSONArray(settings.ignoredPhrasesList))
            put("cleanupPhrases", JSONArray(settings.cleanupPhrasesList))
          }
          put("settings", settingsObj)

          val notifArray = JSONArray()
          ignored.forEach { item ->
            notifArray.put(JSONObject().apply {
              put("packageName", item.packageName)
              put("type", item.type.name)
              put("typeData", item.typeData)
              put("createdAt", item.createdAt.time)
            })
          }
          put("ignoredNotifs", notifArray)
        }

        context.contentResolver.openOutputStream(uri)?.bufferedWriter()?.use {
          it.write(rootJson.toString(2))
        }
        showToast(context, context.getString(R.string.export_success))
      } catch (e: Exception) {
        AppLogger.e("SettingsViewModel", "Export failed", e)
        showToast(context, context.getString(R.string.export_failed))
      }
    }
  }

  fun importConfiguration(context: Context, uri: Uri) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val content = context.contentResolver.openInputStream(uri)?.bufferedReader()?.use {
          it.readText()
        } ?: throw IOException("Empty file")

        val rootJson = JSONObject(content)
        val settingsObj = rootJson.getJSONObject("settings")

        val current = userSettingsRepository.fetchSettings()
        val updatedBuilder = current.toBuilder()
          .setIsAutoCopyEnabled(settingsObj.optBoolean("isAutoCopyEnabled", current.isAutoCopyEnabled))
          .setIsPostNotifEnabled(settingsObj.optBoolean("isPostNotifEnabled", current.isPostNotifEnabled))
          .setIsShowCopyConfirmationEnabled(settingsObj.optBoolean("isShowCopyConfirmationEnabled", current.isShowCopyConfirmationEnabled))
          .setIsHistoryDisabled(settingsObj.optBoolean("isHistoryDisabled", current.isHistoryDisabled))
          .setShouldReplaceCodeInHistory(settingsObj.optBoolean("shouldReplaceCodeInHistory", current.shouldReplaceCodeInHistory))
          .setIsAutoDismissEnabled(settingsObj.optBoolean("isAutoDismissEnabled", current.isAutoDismissEnabled))
          .setIsAutoMarkAsReadEnabled(settingsObj.optBoolean("isAutoMarkAsReadEnabled", current.isAutoMarkAsReadEnabled))
          .setIsShowToastEnabled(settingsObj.optBoolean("isShowToastEnabled", current.isShowToastEnabled))
          .setIsCopyAsNotSensitiveEnabled(settingsObj.optBoolean("isCopyAsNotSensitiveEnabled", current.isCopyAsNotSensitiveEnabled))
          .setIsBroadcastCodeEnabled(settingsObj.optBoolean("isBroadcastCodeEnabled", current.isBroadcastCodeEnabled))
          .setBroadcastTargetPackageName(settingsObj.optString("broadcastTargetPackageName", current.broadcastTargetPackageName))

        val sensitiveArr = settingsObj.optJSONArray("sensitivePhrases")
        if (sensitiveArr != null) {
          val list = (0 until sensitiveArr.length()).map { sensitiveArr.getString(it) }
          updatedBuilder.clearSensitivePhrases().addAllSensitivePhrases(list)
        }
        val ignoredArr = settingsObj.optJSONArray("ignoredPhrases")
        if (ignoredArr != null) {
          val list = (0 until ignoredArr.length()).map { ignoredArr.getString(it) }
          updatedBuilder.clearIgnoredPhrases().addAllIgnoredPhrases(list)
        }
        val cleanupArr = settingsObj.optJSONArray("cleanupPhrases")
        if (cleanupArr != null) {
          val list = (0 until cleanupArr.length()).map { cleanupArr.getString(it) }
          updatedBuilder.clearCleanupPhrases().addAllCleanupPhrases(list)
        }

        userSettingsRepository.saveSettings(updatedBuilder.build())

        val notifArray = rootJson.optJSONArray("ignoredNotifs")
        if (notifArray != null && notifArray.length() > 0) {
          val items = (0 until notifArray.length()).map { idx ->
            val itemObj = notifArray.getJSONObject(idx)
            IgnoredNotif(
              packageName = itemObj.getString("packageName"),
              type = IgnoredNotifType.valueOf(itemObj.getString("type")),
              typeData = itemObj.optString("typeData", ""),
              createdAt = Date(itemObj.optLong("createdAt", System.currentTimeMillis()))
            )
          }
          ignoredNotifsRepository?.insertAll(items)
        }

        showToast(context, context.getString(R.string.import_success))
      } catch (e: Exception) {
        AppLogger.e("SettingsViewModel", "Import failed", e)
        showToast(context, context.getString(R.string.invalid_backup_file))
      }
    }
  }
}
