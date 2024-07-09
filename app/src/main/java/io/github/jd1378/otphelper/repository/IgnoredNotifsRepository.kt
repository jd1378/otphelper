package io.github.jd1378.otphelper.repository

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import io.github.jd1378.otphelper.model.IgnoredNotifsOfPackageName
import kotlinx.coroutines.flow.Flow

@Stable
interface IgnoredNotifsRepository {

  fun get(pageSize: Int = 10): Flow<PagingData<IgnoredNotif>>

  fun getGroupedByPackageName(pageSize: Int = 10): Flow<PagingData<IgnoredNotifsOfPackageName>>

  fun getByPackageName(packageName: String, pageSize: Int = 10): Flow<PagingData<IgnoredNotif>>

  suspend fun isIgnored(
      packageName: String,
      notificationId: String,
      notificationTag: String?
  ): Boolean

  suspend fun setIgnored(
      packageName: String,
      type: IgnoredNotifType,
      typeData: String = "",
  )

  suspend fun exists(
      packageName: String,
      type: IgnoredNotifType,
      typeData: String = "",
  ): Flow<Boolean>

  suspend fun deleteIgnored(ignoredNotif: IgnoredNotif)

  suspend fun deleteIgnored(packageName: String, type: IgnoredNotifType, typeData: String)
}
