package io.github.jd1378.otphelper.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jd1378.otphelper.UserSettings
import io.github.jd1378.otphelper.data.legacy.OldIgnoredNotifSetRepository
import io.github.jd1378.otphelper.data.legacy.OldSettingsRepository
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.getDeepLinkPendingIntent
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.ui.navigation.MainDestinations
import io.github.jd1378.otphelper.utils.CodeExtractorDefaults

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
                    var typeData: String = ""
                    val type: IgnoredNotifType

                    if (splitIgnore.size > 2 && splitIgnore[2] == "nid") {
                      type = IgnoredNotifType.NOTIFICATION_ID
                      typeData = splitIgnore[3]
                    } else {
                      type = IgnoredNotifType.APPLICATION
                    }

                    return@map IgnoredNotif(
                        packageName = packageName,
                        type = type,
                        typeData = typeData,
                    )
                  }
                  return@map null
                }
                .filterNotNull())

    userSettingsRepository.saveSettings(
        UserSettings.getDefaultInstance()
            .toBuilder()
            .setVersion(1)
            .setIsMigrationDone(true)
            .setIsSetupFinished(oldSettingsRepository.getIsSetupFinished())
            .setIsAutoCopyEnabled(oldSettingsRepository.getIsAutoCopyEnabled())
            .setIsPostNotifEnabled(oldSettingsRepository.getIsPostNotifEnabled())
            .setIsShowCopyConfirmationEnabled(true)
            .setIsHistoryDisabled(false)
            .setShouldReplaceCodeInHistory(true)
            .clearSensitivePhrases()
            .addAllSensitivePhrases(CodeExtractorDefaults.sensitivePhrases)
            .clearIgnoredPhrases()
            .addAllIgnoredPhrases(CodeExtractorDefaults.ignoredPhrases)
            .build())

    val isSetupFinished = userSettingsRepository.fetchSettings().isSetupFinished
    if (isSetupFinished) {
      getDeepLinkPendingIntent(applicationContext, MainDestinations.HOME_ROUTE).send()
    }

    return Result.success()
  }
}
