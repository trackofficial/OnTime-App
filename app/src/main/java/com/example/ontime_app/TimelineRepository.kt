package com.example.ontime_app.repo

import androidx.lifecycle.LiveData
import com.example.ontime_app.db.TimelineDao
import com.example.ontime_app.model.TimelineItem

class TimelineRepository(private val dao: TimelineDao) {
    val allItems: LiveData<List<TimelineItem>> = dao.getAll()

    suspend fun insert(item: TimelineItem) = dao.insert(item)
    suspend fun update(item: TimelineItem) = dao.update(item)
    suspend fun delete(id: Long) = dao.deleteById(id)
}