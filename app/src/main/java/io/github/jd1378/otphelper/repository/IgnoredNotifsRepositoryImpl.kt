package io.github.jd1378.otphelper.repository

import androidx.compose.runtime.Stable
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import io.github.jd1378.otphelper.data.local.db.OtpHelperDatabase
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.model.IgnoredNotifsOfPackageName
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

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
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = {
              otpHelperDatabase.ignoredNotifDao().ignoredNotifSortedByCreatedAtPagingSource()
            },
        )
        .flow
  }

  override fun getGroupedByPackageName(
      pageSize: Int
  ): Flow<PagingData<IgnoredNotifsOfPackageName>> {
    return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = { otpHelperDatabase.ignoredNotifDao().getGroupedByPackageName() },
        )
        .flow
  }

  override fun getByPackageName(
      packageName: String,
      pageSize: Int
  ): Flow<PagingData<IgnoredNotif>> {
    return Pager(
            config = PagingConfig(pageSize = pageSize, enablePlaceholders = false),
            pagingSourceFactory = {
              otpHelperDatabase.ignoredNotifDao().ignoredNotifByPackageNamePagingSource(packageName)
            },
        )
        .flow
  }

  override suspend fun isIgnored(
      packageName: String,
      notificationId: String?,
      notificationTag: String?,
      smsOrigin: String?,
  ): Boolean {

    if (!smsOrigin.isNullOrBlank()) {
      return otpHelperDatabase.ignoredNotifDao().ignoredNotifBySmsOrigin(smsOrigin).firstOrNull() !=
          null
    } else {
      val ignoredList = otpHelperDatabase.ignoredNotifDao().ignoredNotifByPackageName(packageName)
      return null !=
          ignoredList.firstOrNull() {
            return when (it.type) {
              IgnoredNotifType.APPLICATION -> true
              IgnoredNotifType.NOTIFICATION_ID -> it.typeData == notificationId
              IgnoredNotifType.NOTIFICATION_TAG ->
                  !notificationTag.isNullOrBlank() && it.typeData == notificationTag
              IgnoredNotifType.SMS_ORIGIN -> false
            }
          }
    }
  }

  override suspend fun setIgnored(
      packageName: String,
      type: IgnoredNotifType,
      typeData: String,
  ) {
    otpHelperDatabase
        .ignoredNotifDao()
        .insert(IgnoredNotif(packageName = packageName, type = type, typeData = typeData))
  }

  override suspend fun exists(
      packageName: String,
      type: IgnoredNotifType,
      typeData: String,
  ): Flow<Boolean> {
    return otpHelperDatabase.ignoredNotifDao().exists(packageName, type, typeData)
  }

  override suspend fun deleteIgnored(ignoredNotif: IgnoredNotif) {
    otpHelperDatabase.ignoredNotifDao().delete(ignoredNotif)
  }

  override suspend fun deleteIgnored(
      packageName: String,
      type: IgnoredNotifType,
      typeData: String,
  ) {
    otpHelperDatabase.ignoredNotifDao().delete(packageName, type, typeData)
  }
}
