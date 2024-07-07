package com.example.alarmscratch.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "alarms")
data class Alarm(
    // TODO: Do @ColumnInfo for custom column names
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String = "",
    val enabled: Boolean = true,
    // TODO: Java error
    val dateTime: LocalDateTime = LocalDateTime.now(),
    val weeklyRepeater: WeeklyRepeater = WeeklyRepeater()
)
