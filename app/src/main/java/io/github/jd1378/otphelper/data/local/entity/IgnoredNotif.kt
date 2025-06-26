package io.github.jd1378.otphelper.data.local.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    indices =
        [
            Index("createdAt"),
            Index("packageName"),
            Index(value = ["packageName", "type", "typeData"], unique = true),
            Index(value = ["type", "typeData"]),
        ],
)
@Immutable
data class IgnoredNotif(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val type: IgnoredNotifType,
    /** Not null when type is anything other than IgnoredNotifType.APPLICATION */
    val typeData: String = "",
    val createdAt: Date = Date(),
)
