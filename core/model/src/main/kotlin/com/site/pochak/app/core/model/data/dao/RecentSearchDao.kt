package com.site.pochak.app.core.model.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.site.pochak.app.core.model.data.RecentSearch

@Dao
interface RecentSearchDao {
    @Query("SELECT * FROM recent_searches ORDER BY timestamp DESC")
    suspend fun getAllRecentSearches(): List<RecentSearch>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecentSearch(recentSearch: RecentSearch)

    @Query("DELETE FROM recent_searches WHERE id = :id")
    suspend fun deleteRecentSearchById(id: String)

    @Query("DELETE FROM recent_searches")
    suspend fun deleteAllRecentSearches()
}