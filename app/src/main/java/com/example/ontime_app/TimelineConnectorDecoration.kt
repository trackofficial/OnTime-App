package com.example.ontime_app.ui.timeline

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

//For this feature I decided to use AI
class TimelineConnectorDecoration(
    private val context: Context,
    private val emojiViewId: Int,
    private val colorRes: Int,
    private val strokeWidthPx: Float,
    private val edgePaddingPx: Float = strokeWidthPx * 4f
) : RecyclerView.ItemDecoration() {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, colorRes)
        style = Paint.Style.STROKE
        strokeWidth = strokeWidthPx
        strokeCap = Paint.Cap.ROUND
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val childCount = parent.childCount
        if (childCount == 0) return

        data class Node(val cx: Float, val cy: Float, val r: Float, val adapterPos: Int)

        val nodes = ArrayList<Node>(childCount)
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i) ?: continue
            val emojiView = child.findViewById<View>(emojiViewId) ?: continue
            val adapterPos = parent.getChildAdapterPosition(child)
            if (adapterPos == RecyclerView.NO_POSITION) continue

            val cx = child.left + (emojiView.left + emojiView.right) / 2f
            val cy = child.top + (emojiView.top + emojiView.bottom) / 2f
            val r = (emojiView.right - emojiView.left) / 2f
            nodes.add(Node(cx, cy, r, adapterPos))
        }

        if (nodes.isEmpty()) return

        for (i in 0 until nodes.size - 1) {
            val a = nodes[i]
            val b = nodes[i + 1]

            val x = (a.cx + b.cx) / 2f
            val startY = a.cy + a.r
            val endY = b.cy - b.r
            if (endY - startY <= strokeWidthPx) continue
            c.drawLine(x, startY, x, endY, paint)
        }

        val first = nodes.first()
        if (first.adapterPos > 0) {
            val topStart = edgePaddingPx
            val topEnd = first.cy - first.r
            if (topEnd - topStart > strokeWidthPx) c.drawLine(first.cx, topStart, first.cx, topEnd, paint)
        }

        val last = nodes.last()
        val adapter = parent.adapter
        val hasMoreBelow = adapter != null && last.adapterPos < (adapter.itemCount - 1)
        if (hasMoreBelow) {
            val bottomStart = last.cy + last.r
            val bottomEnd = parent.height - edgePaddingPx
            if (bottomEnd - bottomStart > strokeWidthPx) c.drawLine(last.cx, bottomStart, last.cx, bottomEnd, paint)
        }
    }
}