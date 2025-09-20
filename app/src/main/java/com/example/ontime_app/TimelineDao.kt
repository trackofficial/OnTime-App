package com.example.ontime_app.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.ontime_app.model.TimelineItem

@Dao
interface TimelineDao {

    @Query("SELECT * FROM timeline_items ORDER BY timeMillis ASC, id ASC")
    fun getAll(): LiveData<List<TimelineItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: TimelineItem)

    @Update
    suspend fun update(item: TimelineItem)

    @Query("DELETE FROM timeline_items WHERE id = :id")
    suspend fun deleteById(id: Long)
}