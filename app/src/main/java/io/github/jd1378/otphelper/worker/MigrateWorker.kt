package io.github.jd1378.otphelper.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jd1378.otphelper.UserSettings
import io.github.jd1378.otphelper.copy
import io.github.jd1378.otphelper.data.legacy.OldIgnoredNotifSetRepository
import io.github.jd1378.otphelper.data.legacy.OldSettingsRepository
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.repository.UserSettingsRepository

const val migrateWorkName = "migrate_work"

@HiltWorker
class MigrateWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val otpHelperDatabase: OtpHelperDatabase,
    private val oldSettingsRepository: OldSettingsRepository,
    private val oldIgnoredNotifSetRepository: OldIgnoredNotifSetRepository,
    private val userSettingsRepository: UserSettingsRepository,
) : CoroutineWorker(context, workerParams) {

  companion object {
    const val TAG: String = "MigrateWorker"
  }

  override suspend fun doWork(): Result {

    val ignoredList = oldIgnoredNotifSetRepository.getIgnoredNotifSet()
    // we do not handle tags here, because it was mistakenly missing packageName
    otpHelperDatabase
        .ignoredNotifDao()
        .insertAll(
            ignoredList
                .map {
                  val splitIgnore = it.split(":")
                  val packageName = if (splitIgnore[0] == "app") splitIgnore[1] else null
                  if (!packageName.isNullOrEmpty()) {
                    val type =
                        if (splitIgnore.size > 2 && splitIgnore[2] == "nid") {
                          IgnoredNotifType.NOTIFICATION_ID
                        } else {
                          IgnoredNotifType.APPLICATION
                        }
                    return@map IgnoredNotif(packageName = packageName, type = type)
                  }
                  return@map null
                }
                .filterNotNull())

    userSettingsRepository.saveSettings(
        UserSettings.getDefaultInstance().copy {
          isMigrationDone = true
          isSetupFinished = oldSettingsRepository.getIsSetupFinished()
          isAutoCopyEnabled = oldSettingsRepository.getIsAutoCopyEnabled()
          isPostNotifEnabled = oldSettingsRepository.getIsPostNotifEnabled()
          isCopiedToastEnabled = true
          isHistoryDisabled = false
          shouldReplaceCodeInHistory = true
        })
    return Result.success()
  }
}
