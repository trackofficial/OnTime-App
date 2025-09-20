package com.example.ontime_app

import android.app.Activity
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import java.util.Locale

class EditNoteActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "id"
        const val EXTRA_EMOJI = "emoji"
        const val EXTRA_TITLE = "title"
        const val EXTRA_DESCRIPTION = "description"
        const val EXTRA_TIME = "time"
    }

    private lateinit var etEmoji: EditText
    private lateinit var etTitle: EditText
    private lateinit var etTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        etEmoji = findViewById(R.id.etEmoji)
        etTitle = findViewById(R.id.etTitle)
        etTime = findViewById(R.id.etTime)
        etDescription = findViewById(R.id.etDescription)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)

        intent?.let {
            if (it.hasExtra(EXTRA_EMOJI)) etEmoji.setText(it.getStringExtra(EXTRA_EMOJI))
            if (it.hasExtra(EXTRA_TITLE)) etTitle.setText(it.getStringExtra(EXTRA_TITLE))
            if (it.hasExtra(EXTRA_DESCRIPTION)) etDescription.setText(it.getStringExtra(EXTRA_DESCRIPTION))
            if (it.hasExtra(EXTRA_TIME)) etTime.setText(it.getStringExtra(EXTRA_TIME))
        }

        etTime.setOnClickListener {
            val parts = etTime.text.toString().split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 12
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            TimePickerDialog(this, { _, h, m ->
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m))
            }, hour, minute, true).show()
        }

        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        btnSave.setOnClickListener {
            val emoji = etEmoji.text.toString().ifBlank { "📝" }
            val title = etTitle.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val time = etTime.text.toString().trim()

            if (title.isEmpty()) {
                etTitle.error = "Введите заголовок"
                return@setOnClickListener
            }
            if (time.isNotEmpty() && !Regex("^([01]\\d|2[0-3]):[0-5]\\d$").matches(time)) {
                etTime.error = "Неверный формат времени"
                return@setOnClickListener
            }

            val result = Intent().apply {
                putExtra(EXTRA_ID, intent.getLongExtra(EXTRA_ID, -1L))
                putExtra(EXTRA_EMOJI, emoji)
                putExtra(EXTRA_TITLE, title)
                putExtra(EXTRA_DESCRIPTION, description)
                putExtra(EXTRA_TIME, time)
            }
            setResult(Activity.RESULT_OK, result)
            finish()
        }
    }
}