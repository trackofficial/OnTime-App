package com.example.ontime_app.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "timeline_items")
data class TimelineItem(
    @PrimaryKey val id: Long,
    val emoji: String = "📝",
    val title: String,
    val description: String,
    val time: String = "",
    val timeMillis: Long = 0L
) : Parcelable