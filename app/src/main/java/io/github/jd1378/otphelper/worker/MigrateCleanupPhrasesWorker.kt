package io.github.jd1378.otphelper.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jd1378.otphelper.repository.UserSettingsRepository
import io.github.jd1378.otphelper.utils.CodeExtractorDefaults

const val migrateCleanupPhrasesWorkName = "cleanup_phrases_migrate_work"

@HiltWorker
class MigrateCleanupPhrasesWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val userSettingsRepository: UserSettingsRepository,
) : CoroutineWorker(context, workerParams) {

  companion object {
    const val TAG: String = "MigrateWorker"
  }

  override suspend fun doWork(): Result {

    userSettingsRepository.setCleanupPhrases(CodeExtractorDefaults.cleanupPhrases)
    userSettingsRepository.setIsCleanupPhrasesMigrated(true)

    return Result.success()
  }
}
