package io.github.jd1378.otphelper.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.jd1378.otphelper.data.local.entity.DetectedCode
import kotlinx.coroutines.flow.Flow

@Dao
abstract class DetectedCodeDao : BaseDao<DetectedCode> {

  @Transaction
  @Query("SELECT * FROM DetectedCode ORDER BY createdAt DESC")
  abstract fun detectedCodeSortedByCreatedAtPagingSource(): PagingSource<Int, DetectedCode>

  @Query("SELECT * FROM DetectedCode WHERE id = :id ORDER BY createdAt DESC")
  abstract fun getById(id: Long): Flow<DetectedCode?>

  @Query("DELETE FROM DetectedCode WHERE id = :id") abstract suspend fun delete(id: String)

  @Query(
      "DELETE FROM DetectedCode WHERE id NOT IN (SELECT id FROM DetectedCode ORDER BY createdAt DESC LIMIT :keepOnly)")
  abstract suspend fun cleanup(keepOnly: Int = 10)

  @Query("DELETE FROM DetectedCode") abstract suspend fun deleteAll()
}
