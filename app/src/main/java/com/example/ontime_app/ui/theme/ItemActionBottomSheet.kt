package com.example.ontime_app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.ontime_app.R
import com.example.ontime_app.model.TimelineItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton

class ItemActionBottomSheet : BottomSheetDialogFragment() {

    var onEdit: ((TimelineItem) -> Unit)? = null
    var onDelete: ((TimelineItem) -> Unit)? = null

    companion object {
        private const val ARG_ITEM = "arg_item"
        fun newInstance(item: TimelineItem): ItemActionBottomSheet {
            val f = ItemActionBottomSheet()
            val args = Bundle()
            args.putParcelable(ARG_ITEM, item)
            f.arguments = args
            return f
        }
    }

    private var item: TimelineItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        item = arguments?.getParcelable(ARG_ITEM)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.bs_item_actions, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tvTime = view.findViewById<TextView>(R.id.bsTime)
        val tvTitle = view.findViewById<TextView>(R.id.bsTitle)
        val tvDesc = view.findViewById<TextView>(R.id.bsDescription)
        val btnEdit = view.findViewById<MaterialButton>(R.id.btnEdit)
        val btnDelete = view.findViewById<MaterialButton>(R.id.btnDelete)

        item?.let { itItem ->
            tvTime.text = itItem.time
            tvTitle.text = itItem.title
            tvDesc.text = itItem.description
        }

        btnEdit.setOnClickListener {
            item?.let { onEdit?.invoke(it) }
            dismiss()
        }
        btnDelete.setOnClickListener {
            item?.let { onDelete?.invoke(it) }
            dismiss()
        }
    }
}