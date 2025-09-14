package com.example.ontime_app.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.Calendar

class DateViewModel : ViewModel() {

    private val _selectedDate = MutableLiveData<Calendar>()
    val selectedDate: LiveData<Calendar> = _selectedDate

    private val _activeDates = MutableLiveData<Set<Long>>()
    val activeDates: LiveData<Set<Long>> = _activeDates

    init {
        _selectedDate.value = Calendar.getInstance()
        _activeDates.value = emptySet() // Загрузи реальные даты из DAO, если нужно
    }

    fun selectDate(date: Calendar) {
        _selectedDate.value = date
    }

    fun getActiveDates(): Set<Long> {
        return _activeDates.value ?: emptySet()
    }
}