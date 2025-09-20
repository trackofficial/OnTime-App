package com.example.ontime_app.ui

import android.content.Context
import android.content.Intent
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.ontime_app.EditNoteActivity
import com.example.ontime_app.R
import com.example.ontime_app.model.TimelineItem

class TimelineAdapter(
    private val ctx: Context,
    private val onEditRequest: ((Intent) -> Unit)? = null,
    private val callback: AdapterCallback? = null
) : ListAdapter<TimelineItem, TimelineAdapter.VH>(DIFF) {

    interface AdapterCallback {
        fun onDelete(id: Long)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_timeline, parent, false)
        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val emoji: TextView = itemView.findViewById(R.id.ivEmoji)
        private val title: TextView = itemView.findViewById(R.id.title)
        private val description: TextView = itemView.findViewById(R.id.description)
        private val btnMore: ImageButton = itemView.findViewById(R.id.btnMore)
        private val contentRoot: LinearLayout = itemView.findViewById(R.id.contentRoot)

        fun bind(item: TimelineItem) {
            emoji.text = item.emoji
            title.text = item.title
            description.text = item.description

            if (item.description.isBlank()) {
                description.visibility = View.GONE
                contentRoot.gravity = Gravity.CENTER_VERTICAL
            } else {
                description.visibility = View.VISIBLE
                contentRoot.gravity = Gravity.TOP
            }
            itemView.setOnClickListener(null)

            btnMore.setOnClickListener {
                val sheet = ItemActionBottomSheet.newInstance(item)
                sheet.onEdit = { i ->
                    val intent = Intent(ctx, EditNoteActivity::class.java).apply {
                        putExtra(EditNoteActivity.EXTRA_ID, i.id)
                        putExtra(EditNoteActivity.EXTRA_EMOJI, i.emoji)
                        putExtra(EditNoteActivity.EXTRA_TITLE, i.title)
                        putExtra(EditNoteActivity.EXTRA_DESCRIPTION, i.description)
                        putExtra(EditNoteActivity.EXTRA_TIME, i.time)
                    }
                    onEditRequest?.invoke(intent) ?: ctx.startActivity(intent)
                }
                sheet.onDelete = { i ->
                    callback?.onDelete(i.id)
                }
                val fm = (ctx as AppCompatActivity).supportFragmentManager
                sheet.show(fm, "item_actions")
            }
        }
    }

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<TimelineItem>() {
            override fun areItemsTheSame(oldItem: TimelineItem, newItem: TimelineItem): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: TimelineItem, newItem: TimelineItem): Boolean =
                oldItem == newItem
        }
    }
}