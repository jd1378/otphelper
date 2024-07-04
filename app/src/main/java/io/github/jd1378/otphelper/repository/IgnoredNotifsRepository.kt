package io.github.jd1378.otphelper.repository

import androidx.compose.runtime.Stable
import androidx.paging.PagingData
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotif
import io.github.jd1378.otphelper.data.local.entity.IgnoredNotifType
import kotlinx.coroutines.flow.Flow

@Stable
interface IgnoredNotifsRepository {

  fun get(pageSize: Int = 10): Flow<PagingData<IgnoredNotif>>

  suspend fun isIgnored(
      packageName: String,
      notificationId: String,
      notificationTag: String?
  ): Boolean

  suspend fun setIgnored(
      packageName: String,
      type: IgnoredNotifType,
      typeData: String? = null,
  )

  suspend fun deleteIgnored(ignoredNotif: IgnoredNotif)
}
