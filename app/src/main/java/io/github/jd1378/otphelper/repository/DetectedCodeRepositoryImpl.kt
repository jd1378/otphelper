package io.github.jd1378.otphelper.repository

import androidx.compose.runtime.Stable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import io.github.jd1378.otphelper.data.local.entity.DetectedCode
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Stable
class DetectedCodeRepositoryImpl
@Inject
constructor(
    private val otpHelperDatabase: OtpHelperDatabase,
) : DetectedCodeRepository {

  companion object {
    const val TAG = "DetectedCodeRepositoryImpl"
  }

  override fun get(pageSize: Int): Flow<PagingData<DetectedCode>> {
    return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = {
              otpHelperDatabase.detectedCodeDao().detectedCodeSortedByCreatedAtPagingSource()
            },
        )
        .flow
  }

  override fun getById(historyId: Long): Flow<DetectedCode?> {
    return otpHelperDatabase.detectedCodeDao().getById(historyId)
  }

  override suspend fun deleteDetectedCode(detectedCode: DetectedCode) {
    otpHelperDatabase.detectedCodeDao().delete(detectedCode)
  }

  override suspend fun deleteAll() {
    otpHelperDatabase.detectedCodeDao().deleteAll()
  }
}
