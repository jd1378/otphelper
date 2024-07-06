package io.github.jd1378.otphelper.data.local.entity

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    indices = [Index("createdAt")],
)
@Immutable
data class DetectedCode(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val packageName: String,
    val notificationId: String,
    val notificationTag: String = "",
    val text: String,
    val createdAt: Date = Date(),
)
