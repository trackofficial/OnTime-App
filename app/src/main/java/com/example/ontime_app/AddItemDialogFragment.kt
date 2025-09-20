package com.example.ontime_app

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import androidx.fragment.app.DialogFragment
import com.example.ontime_app.model.TimelineItem

class AddItemDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_ITEM = "arg_item"

        fun newInstance(item: TimelineItem?): AddItemDialogFragment {
            val frag = AddItemDialogFragment()
            val args = Bundle()
            args.putParcelable(ARG_ITEM, item as Parcelable?)
            frag.arguments = args
            return frag
        }
    }

    private var editingItem: TimelineItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editingItem = arguments?.getParcelable(ARG_ITEM)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        TODO("Dialog implementation")
    }
}