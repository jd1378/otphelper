package io.github.jd1378.otphelper.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase

const val dataCleanupWorkName = "data_cleanup_work"

@HiltWorker
class DataCleanupWorker
@AssistedInject
constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val otpHelperDatabase: OtpHelperDatabase,
) : CoroutineWorker(context, workerParams) {

  companion object {
    const val TAG: String = "DataCleanupWorker"
  }

  override suspend fun doWork(): Result {
    otpHelperDatabase.detectedCodeDao().cleanup()
    return Result.success()
  }
}
