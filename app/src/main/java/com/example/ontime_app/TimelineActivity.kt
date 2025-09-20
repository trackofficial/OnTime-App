package com.example.ontime_app

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ontime_app.model.TimelineItem
import com.example.ontime_app.ui.TimelineAdapter
import com.example.ontime_app.ui.timeline.TimelineConnectorDecoration

class TimelineActivity : AppCompatActivity() {

    private lateinit var viewModel: TimelineViewModel
    private lateinit var adapter: TimelineAdapter
    private lateinit var recycler: RecyclerView

    private val editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        Log.d("TimelineActivity", "onActivityResult code=${res.resultCode}")
        if (res.resultCode == Activity.RESULT_OK) {
            val data = res.data ?: return@registerForActivityResult
            val id = data.getLongExtra(EditNoteActivity.EXTRA_ID, -1L)
            val emoji = data.getStringExtra(EditNoteActivity.EXTRA_EMOJI) ?: "📝"
            val title = data.getStringExtra(EditNoteActivity.EXTRA_TITLE) ?: return@registerForActivityResult
            val desc = data.getStringExtra(EditNoteActivity.EXTRA_DESCRIPTION) ?: ""
            val time = data.getStringExtra(EditNoteActivity.EXTRA_TIME) ?: ""

            if (id >= 0) {
                viewModel.update(TimelineItem(id, emoji = emoji, title = title, description = desc, time = time))
            } else {
                val newId = System.currentTimeMillis()
                viewModel.create(TimelineItem(newId, emoji = emoji, title = title, description = desc, time = time))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timeline)

        viewModel = ViewModelProvider(this)[TimelineViewModel::class.java]

        recycler = findViewById(R.id.recyclerTimeline)
        recycler.layoutManager = LinearLayoutManager(this)

        adapter = TimelineAdapter(this, onEditRequest = { intent ->
            editLauncher.launch(intent)
        }, callback = object : TimelineAdapter.AdapterCallback {
            override fun onDelete(id: Long) {
                viewModel.delete(id)
            }
        })
        recycler.adapter = adapter

        val strokePx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
        val decoration = TimelineConnectorDecoration(this, R.id.ivEmoji, android.R.color.black, strokePx)
        recycler.addItemDecoration(decoration)

        findViewById<com.google.android.material.floatingactionbutton.FloatingActionButton>(R.id.fabAdd)
            .setOnClickListener {
                val intent = Intent(this, EditNoteActivity::class.java)
                editLauncher.launch(intent)
            }

        viewModel.blocks.observe(this) { list ->
            Log.d("TimelineActivity", "LiveData emitted ${list.size} items")
            adapter.submitList(list)
        }

        // demo content if empty
        viewModel.blocks.value?.let {
            if (it.isEmpty()) {
                viewModel.create(TimelineItem(System.currentTimeMillis() + 1, "✈️", "Flight", "Fly to city", "09:30"))
                viewModel.create(TimelineItem(System.currentTimeMillis() + 2, "📷", "Photos", "Take photos", "14:00"))
                viewModel.create(TimelineItem(System.currentTimeMillis() + 3, "🌳", "Park", "Walk in park", "18:00"))
            }
        }
    }
}