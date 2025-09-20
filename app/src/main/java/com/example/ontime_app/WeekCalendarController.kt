package com.example.ontime_app.calendar

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.children
import com.example.ontime_app.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class WeekCalendarController(
    private val context: Context,
    private val container: LinearLayout,
    private val activeDates: Set<Long>,
    private val onDateSelected: (Calendar) -> Unit
) {
    private var lastRenderedWeekStart: Long = -1L

    fun renderWeek() {
        val currentWeekStart = Calendar.getInstance().apply {
            set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        val currentWeekStartMillis = currentWeekStart.timeInMillis

        if (currentWeekStartMillis == lastRenderedWeekStart) return

        lastRenderedWeekStart = currentWeekStartMillis

        container.removeAllViews()
        val calendar = currentWeekStart.clone() as Calendar
        val dayFormat = SimpleDateFormat("EEE", Locale.ENGLISH)

        repeat(7) {
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val dayOfWeek = dayFormat.format(calendar.time).uppercase(Locale.ENGLISH)
            val view = createDateView(day, dayOfWeek)
            applyStyle(view, calendar)
            view.setOnClickListener { onDateSelected(calendar.clone() as Calendar) }
            container.addView(view)
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }
    }

    private fun createDateView(day: Int, dayOfWeek: String): LinearLayout {
        val layout = LinearLayout(context).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setMargins(12, 8, 12, 8)
            }
            minimumWidth = 100
        }

        val dayText = TextView(context).apply {
            text = day.toString()
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f)
            typeface = ResourcesCompat.getFont(context, R.font.tilda_sans_bold)
            setTextColor(Color.parseColor("#595762"))
            gravity = Gravity.CENTER
        }

        val weekText = TextView(context).apply {
            text = dayOfWeek
            setTextSize(TypedValue.COMPLEX_UNIT_SP, 13f)
            typeface = ResourcesCompat.getFont(context, R.font.tilda_sans_extrabold)
            setTextColor(Color.parseColor("#595762"))
            gravity = Gravity.CENTER
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.CENTER_HORIZONTAL
            }
        }

        val underline = View(context).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                8
            ).apply {
                topMargin = 12
                gravity = Gravity.CENTER_HORIZONTAL
            }
            setBackgroundColor(Color.TRANSPARENT)
            tag = "underline"
        }

        layout.addView(dayText)
        layout.addView(weekText)
        layout.addView(underline)
        return layout
    }

    private fun applyStyle(layout: LinearLayout, calendar: Calendar) {
        val today = Calendar.getInstance()
        val isToday = calendar.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                calendar.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR)

        val hasPlans = checkIfDayHasPlans(calendar)

        val textColor = when {
            isToday -> Color.WHITE
            hasPlans -> Color.BLACK
            else -> Color.parseColor("#595762")
        }

        layout.children.forEach { child ->
            if (child is TextView) {
                child.setTextColor(textColor)
            }
            if (child.tag == "underline" && child is View) {
                child.setBackgroundColor(if (isToday) Color.WHITE else Color.TRANSPARENT)
            }
        }
    }

    private fun checkIfDayHasPlans(calendar: Calendar): Boolean {
        val start = calendar.clone() as Calendar
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        start.set(Calendar.SECOND, 0)
        start.set(Calendar.MILLISECOND, 0)

        val end = calendar.clone() as Calendar
        end.set(Calendar.HOUR_OF_DAY, 23)
        end.set(Calendar.MINUTE, 59)
        end.set(Calendar.SECOND, 59)
        end.set(Calendar.MILLISECOND, 999)

        return activeDates.any { it in start.timeInMillis..end.timeInMillis }
    }
}