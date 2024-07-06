package io.github.jd1378.otphelper.repository

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import io.github.jd1378.otphelper.data.local.entity.DetectedCode
import kotlinx.coroutines.flow.Flow

@Stable
interface DetectedCodeRepository {

  fun get(pageSize: Int = 8): Flow<PagingData<DetectedCode>>

  fun getById(historyId: Long): Flow<DetectedCode?>

  suspend fun deleteDetectedCode(detectedCode: DetectedCode)

  suspend fun deleteAll()
}
