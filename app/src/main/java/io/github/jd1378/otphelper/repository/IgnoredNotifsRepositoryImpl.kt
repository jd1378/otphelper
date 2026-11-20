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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Singleton
@Stable
class IgnoredNotifsRepositoryImpl
@Inject
constructor(
    private val otpHelperDatabase: OtpHelperDatabase,
) : IgnoredNotifsRepository {

  private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
  @Volatile private var cachedIgnoredList: List<IgnoredNotif>? = null

  init {
    coroutineScope.launch {
      otpHelperDatabase.ignoredNotifDao().getAllFlow().collect { list ->
        cachedIgnoredList = list
      }
    }
  }

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
    val list = cachedIgnoredList
    if (list != null) {
      if (!smsOrigin.isNullOrBlank()) {
        return list.any { it.type == IgnoredNotifType.SMS_ORIGIN && it.typeData == smsOrigin }
      } else {
        return isNotificationIgnored(
            list.filter { it.packageName == packageName },
            notificationId,
            notificationTag,
        )
      }
    }

    if (!smsOrigin.isNullOrBlank()) {
      return otpHelperDatabase.ignoredNotifDao().ignoredNotifBySmsOrigin(smsOrigin).firstOrNull() !=
          null
    } else {
      return isNotificationIgnored(
          otpHelperDatabase.ignoredNotifDao().ignoredNotifByPackageName(packageName),
          notificationId,
          notificationTag,
      )
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

/** [entries] are the ignored entries of the notification's package. */
internal fun isNotificationIgnored(
    entries: List<IgnoredNotif>,
    notificationId: String?,
    notificationTag: String?,
): Boolean =
    entries.any {
      when (it.type) {
        IgnoredNotifType.APPLICATION -> true
        IgnoredNotifType.NOTIFICATION_ID -> it.typeData == notificationId
        IgnoredNotifType.NOTIFICATION_TAG ->
            !notificationTag.isNullOrBlank() && it.typeData == notificationTag
        IgnoredNotifType.SMS_ORIGIN -> false
      }
    }
