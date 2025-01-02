package com.site.pochak.app.core.model.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "recent_searches")
data class RecentSearch(
    @PrimaryKey val id: String,
    val handle: String,
    val name: String,
    val profileImage: String,
    val timestamp: Long
)