package io.github.jd1378.otphelper.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.model.IgnoredNotifsOfPackageName
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

  @Query("SELECT * FROM IgnoredNotif WHERE packageName = :packageName ORDER BY type ASC")
  abstract fun ignoredNotifByPackageNamePagingSource(
      packageName: String
  ): PagingSource<Int, IgnoredNotif>

  @Query(
      "SELECT 1 FROM IgnoredNotif WHERE packageName = :packageName AND type = :type AND typeData = :typeData LIMIT 1")
  abstract fun exists(packageName: String, type: IgnoredNotifType, typeData: String?): Flow<Boolean>

  @Query("DELETE FROM IgnoredNotif WHERE id = :id") abstract suspend fun delete(id: String)

  @Query(
      "DELETE FROM IgnoredNotif WHERE packageName = :packageName AND type = :type AND typeData = :typeData")
  abstract suspend fun delete(packageName: String, type: IgnoredNotifType, typeData: String)

  @Query("SELECT packageName, count(*) as totalItems FROM IgnoredNotif GROUP BY packageName")
  abstract fun getGroupedByPackageName(): PagingSource<Int, IgnoredNotifsOfPackageName>
}
