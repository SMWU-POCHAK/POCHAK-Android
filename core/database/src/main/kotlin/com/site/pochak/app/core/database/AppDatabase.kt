package com.site.pochak.app.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.site.pochak.app.core.model.data.RecentSearch
import com.site.pochak.app.core.database.dao.RecentSearchDao

@Database(entities = [RecentSearch::class], version = 1, exportSchema = true)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recentSearchDao(): RecentSearchDao
}