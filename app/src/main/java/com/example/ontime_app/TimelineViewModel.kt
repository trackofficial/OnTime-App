package com.example.ontime_app

import androidx.lifecycle.*
import com.example.ontime_app.model.TimelineItem
import com.example.ontime_app.repo.TimelineRepository
import kotlinx.coroutines.launch

class TimelineViewModel(private val repo: TimelineRepository) : ViewModel() {

    val blocks: LiveData<List<TimelineItem>> = repo.allItems

    fun create(item: TimelineItem) = viewModelScope.launch { repo.insert(item) }
    fun update(item: TimelineItem) = viewModelScope.launch { repo.update(item) }
    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }

    class Factory(private val repo: TimelineRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TimelineViewModel::class.java)) {
                return TimelineViewModel(repo) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}