package com.example.ontime_app

import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import com.example.ontime_app.calendar.WeekCalendarController
import java.util.Calendar

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_layout)

        val calendarContainer = findViewById<LinearLayout>(R.id.weekCalendarContainer)

        val activeDates = emptySet<Long>() // Загрузи реальные даты, если нужно

        val controller = WeekCalendarController(
            context = this,
            container = calendarContainer,
            activeDates = activeDates,
            onDateSelected = { selectedDate ->
                // Обработка выбора даты
            }
        )

        controller.renderWeek()
    }
}