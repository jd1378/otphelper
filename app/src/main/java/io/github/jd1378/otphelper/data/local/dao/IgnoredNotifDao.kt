package io.github.jd1378.otphelper.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import kotlinx.coroutines.flow.Flow

@Dao
abstract class IgnoredNotifDao : BaseDao<IgnoredNotif> {

  @Transaction
  @Query("SELECT * FROM IgnoredNotif ORDER BY createdAt DESC")
  abstract fun ignoredNotifSortedByCreatedAtPagingSource(): PagingSource<Int, IgnoredNotif>

  @Query("SELECT * FROM IgnoredNotif WHERE id = :id ORDER BY createdAt DESC")
  abstract fun ignoredNotifById(id: String): Flow<IgnoredNotif?>

  @Query("SELECT * FROM IgnoredNotif WHERE packageName = :packageName")
  abstract suspend fun ignoredNotifByPackageName(packageName: String): List<IgnoredNotif>

  @Query("DELETE FROM IgnoredNotif WHERE id = :id") abstract suspend fun delete(id: String)
}
