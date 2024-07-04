package io.github.jd1378.otphelper.data.local.entity

import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    indices = [Index("createdAt"), Index("packageName"), Index("packageName", "type")],
)
@Stable
data class IgnoredNotif(
    @PrimaryKey val id: Int = 0,
    val packageName: String,
    val type: IgnoredNotifType,
    /** Not null when type is anything other than IgnoredNotifType.APPLICATION */
    val typeData: String? = null,
    val createdAt: Date = Date(),
)
