package io.github.jd1378.otphelper.repository

import androidx.compose.runtime.Stable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Stable
class IgnoredNotifsRepositoryImpl
@Inject
constructor(
    private val otpHelperDatabase: OtpHelperDatabase,
) : IgnoredNotifsRepository {

  companion object {
    const val TAG = "IgnoredNotifsRepositoryImpl"
  }

  override fun get(pageSize: Int): Flow<PagingData<IgnoredNotif>> {
    return Pager(
            config = PagingConfig(pageSize = pageSize),
            pagingSourceFactory = {
              otpHelperDatabase.ignoredNotifDao().ignoredNotifSortedByCreatedAtPagingSource()
            },
        )
        .flow
  }

  override suspend fun isIgnored(
      packageName: String,
      notificationId: String,
      notificationTag: String?
  ): Boolean {

    val ignoredList = otpHelperDatabase.ignoredNotifDao().ignoredNotifByPackageName(packageName)
    return null !=
        ignoredList.firstOrNull() {
          return when (it.type) {
            IgnoredNotifType.APPLICATION -> true
            IgnoredNotifType.NOTIFICATION_ID -> it.typeData == notificationId
            IgnoredNotifType.NOTIFICATION_TAG ->
                notificationTag != null && it.typeData == notificationTag
          }
        }
  }

  override suspend fun setIgnored(
      packageName: String,
      type: IgnoredNotifType,
      typeData: String?,
  ) {
    otpHelperDatabase
        .ignoredNotifDao()
        .insert(IgnoredNotif(packageName = packageName, type = type, typeData = typeData))
  }

  override suspend fun deleteIgnored(ignoredNotif: IgnoredNotif) {
    otpHelperDatabase.ignoredNotifDao().delete(ignoredNotif)
  }
}
