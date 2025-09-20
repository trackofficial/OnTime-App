package com.example.ontime_app

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.ontime_app.calendar.WeekCalendarController
import com.example.ontime_app.databinding.MainLayoutBinding
import com.example.ontime_app.db.AppDatabase
import com.example.ontime_app.repo.TimelineRepository
import com.example.ontime_app.ui.TimelineAdapter
import com.example.ontime_app.ui.timeline.TimelineConnectorDecoration

class MainActivity : AppCompatActivity() {

    private lateinit var binding: MainLayoutBinding
    private lateinit var viewModel: TimelineViewModel
    private lateinit var adapter: TimelineAdapter

    private val editLauncher = registerForActivityResult(androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult()) { res ->
        if (res.resultCode == RESULT_OK) {
            val data = res.data ?: return@registerForActivityResult
            val id = data.getLongExtra(EditNoteActivity.EXTRA_ID, -1L)
            val emoji = data.getStringExtra(EditNoteActivity.EXTRA_EMOJI) ?: "📝"
            val title = data.getStringExtra(EditNoteActivity.EXTRA_TITLE) ?: return@registerForActivityResult
            val desc = data.getStringExtra(EditNoteActivity.EXTRA_DESCRIPTION) ?: ""
            val time = data.getStringExtra(EditNoteActivity.EXTRA_TIME) ?: ""

            if (id >= 0) {
                viewModel.update(com.example.ontime_app.model.TimelineItem(id, emoji = emoji, title = title, description = desc, time = time))
            } else {
                val newId = System.currentTimeMillis()
                viewModel.create(com.example.ontime_app.model.TimelineItem(newId, emoji = emoji, title = title, description = desc, time = time))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val db = AppDatabase.getInstance(applicationContext)
        val repo = TimelineRepository(db.timelineDao())
        val factory = TimelineViewModel.Factory(repo)
        viewModel = ViewModelProvider(this, factory)[TimelineViewModel::class.java]

        // Calendar
        val calendarContainer: LinearLayout = binding.weekCalendarContainer
        val controller = WeekCalendarController(
            context = this,
            container = calendarContainer,
            activeDates = emptySet(),
            onDateSelected = { selectedDate ->
                Log.d("MainActivity", "Selected date: $selectedDate")
            }
        )
        controller.renderWeek()

        adapter = TimelineAdapter(
            this,
            onEditRequest = { intent -> editLauncher.launch(intent) },
            callback = object : TimelineAdapter.AdapterCallback {
                override fun onDelete(id: Long) {
                    viewModel.delete(id)
                }
            }
        )

        binding.blockList.layoutManager = LinearLayoutManager(this)
        binding.blockList.adapter = adapter

        val strokePx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics
        )
        val decoration = TimelineConnectorDecoration(
            context = this,
            emojiViewId = com.example.ontime_app.R.id.ivEmoji,
            colorRes = com.example.ontime_app.R.color.stroke_color,
            strokeWidthPx = strokePx
        )
        binding.blockList.addItemDecoration(decoration)

        viewModel.blocks.observe(this) { list ->
            Log.d("MainActivity", "Received ${list.size} blocks from ViewModel")
            adapter.submitList(list)
        }

        binding.btnCreateBlock.setOnClickListener {
            val sheet = CreateBlockBottomSheet.newInstance()
            sheet.onSaved = { item ->
                viewModel.create(item)
            }
            sheet.show(supportFragmentManager, "createBlock")
        }
    }
}