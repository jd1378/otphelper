package io.github.jd1378.otphelper

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.Operation
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import io.github.jd1378.otphelper.utils.getTimeToMidnightMillis
import io.github.jd1378.otphelper.worker.DataCleanupWorker
import io.github.jd1378.otphelper.worker.MigrateCleanupPhrasesWorker
import io.github.jd1378.otphelper.worker.MigrateWorker
import io.github.jd1378.otphelper.worker.RebindListenersWorker
import io.github.jd1378.otphelper.worker.dataCleanupWorkName
import io.github.jd1378.otphelper.worker.migrateCleanupPhrasesWorkName
import io.github.jd1378.otphelper.worker.migrateWorkName
import io.github.jd1378.otphelper.worker.rebindListenersWorkName
import java.util.concurrent.TimeUnit

object MyWorkManager {
  fun doDataMigration(context: Context): Operation {
    val migrateWork = OneTimeWorkRequestBuilder<MigrateWorker>().build()
    return WorkManager.getInstance(context)
        .enqueueUniqueWork(
            migrateWorkName,
            ExistingWorkPolicy.KEEP,
            migrateWork,
        )
  }

  fun doCleanupPhrasesMigration(context: Context): Operation {
    val migrateWork = OneTimeWorkRequestBuilder<MigrateCleanupPhrasesWorker>().build()
    return WorkManager.getInstance(context)
        .enqueueUniqueWork(
            migrateCleanupPhrasesWorkName,
            ExistingWorkPolicy.KEEP,
            migrateWork,
        )
  }

  fun enableHistoryCleanup(context: Context): Operation {
    val cleanupWorkRequest =
        PeriodicWorkRequestBuilder<DataCleanupWorker>(1, TimeUnit.DAYS)
            .setConstraints(Constraints.Builder().setRequiresBatteryNotLow(true).build())
            .setInitialDelay(
                getTimeToMidnightMillis(), // run at midnights
                TimeUnit.MILLISECONDS,
            )
            .build()
    return WorkManager.getInstance(context)
        .enqueueUniquePeriodicWork(
            dataCleanupWorkName,
            ExistingPeriodicWorkPolicy.UPDATE,
            cleanupWorkRequest,
        )
  }

  fun disableHistoryCleanup(context: Context): Operation {
    return WorkManager.getInstance(context).cancelUniqueWork(dataCleanupWorkName)
  }

  fun rebindListeners(context: Context, silent: Boolean = false): Operation {
    val data =
        workDataOf(
            "silent" to silent,
        )

    val migrateWork = OneTimeWorkRequestBuilder<RebindListenersWorker>().setInputData(data).build()

    return WorkManager.getInstance(context)
        .enqueueUniqueWork(
            rebindListenersWorkName,
            ExistingWorkPolicy.KEEP,
            migrateWork,
        )
  }
}
