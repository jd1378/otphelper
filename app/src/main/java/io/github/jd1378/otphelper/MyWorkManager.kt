package io.github.jd1378.otphelper

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import io.github.jd1378.otphelper.utils.getTimeToMidnightMillis
import io.github.jd1378.otphelper.worker.DataCleanupWorker
import io.github.jd1378.otphelper.worker.MigrateCleanupPhrasesWorker
import io.github.jd1378.otphelper.worker.MigrateWorker
import io.github.jd1378.otphelper.worker.dataCleanupWorkName
import io.github.jd1378.otphelper.worker.migrateCleanupPhrasesWorkName
import io.github.jd1378.otphelper.worker.migrateWorkName
import java.util.concurrent.TimeUnit

object MyWorkManager {
  fun doDataMigration(context: Context) {
    val migrateWork = OneTimeWorkRequestBuilder<MigrateWorker>().build()
    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            migrateWorkName,
            ExistingWorkPolicy.KEEP,
            migrateWork,
        )
  }

  fun doCleanupPhrasesMigration(context: Context) {
    val migrateWork = OneTimeWorkRequestBuilder<MigrateCleanupPhrasesWorker>().build()
    WorkManager.getInstance(context)
        .enqueueUniqueWork(
            migrateCleanupPhrasesWorkName,
            ExistingWorkPolicy.KEEP,
            migrateWork,
        )
  }

  fun enableHistoryCleanup(context: Context) {
    val cleanupWorkRequest =
        PeriodicWorkRequestBuilder<DataCleanupWorker>(1, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .setInitialDelay(
                getTimeToMidnightMillis(), // run at midnights
                TimeUnit.MILLISECONDS,
            )
            .build()
    WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            dataCleanupWorkName,
            ExistingPeriodicWorkPolicy.UPDATE,
            cleanupWorkRequest,
        )
  }

  fun disableHistoryCleanup(context: Context) {
    WorkManager.getInstance(context).cancelUniqueWork(dataCleanupWorkName)
  }
}
