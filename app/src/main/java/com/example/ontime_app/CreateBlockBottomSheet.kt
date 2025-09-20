package com.example.ontime_app

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import com.example.ontime_app.model.TimelineItem
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Locale

class CreateBlockBottomSheet : BottomSheetDialogFragment() {

    /**
     * Лямбда, вызываемая при сохранении блока.
     * Устанавливается извне (MainActivity) перед show().
     */
    var onSaved: ((TimelineItem) -> Unit)? = null

    private var editingId: Long? = null

    private lateinit var etEmoji: EditText
    private lateinit var etTitle: EditText
    private lateinit var etTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    companion object {
        private const val ARG_ID = "arg_id"
        private const val ARG_EMOJI = "arg_emoji"
        private const val ARG_TITLE = "arg_title"
        private const val ARG_DESCRIPTION = "arg_description"
        private const val ARG_TIME = "arg_time"

        fun newInstance(
            id: Long? = null,
            emoji: String? = null,
            title: String? = null,
            description: String? = null,
            time: String? = null
        ): CreateBlockBottomSheet {
            val frag = CreateBlockBottomSheet()
            val args = Bundle()
            id?.let { args.putLong(ARG_ID, it) }
            emoji?.let { args.putString(ARG_EMOJI, it) }
            title?.let { args.putString(ARG_TITLE, it) }
            description?.let { args.putString(ARG_DESCRIPTION, it) }
            time?.let { args.putString(ARG_TIME, it) }
            frag.arguments = args
            return frag
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = BottomSheetDialog(requireContext(), theme)
        dialog.setOnShowListener { dlg ->
            val d = dlg as BottomSheetDialog
            val sheet = d.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            sheet?.let {
                BottomSheetBehavior.from(it).state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
        return dialog
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_create_block_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etEmoji = view.findViewById(R.id.etEmoji)
        etTitle = view.findViewById(R.id.etTitle)
        etTime = view.findViewById(R.id.etTime)
        etDescription = view.findViewById(R.id.etDescription)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        arguments?.let { args ->
            if (args.containsKey(ARG_ID)) editingId = args.getLong(ARG_ID)
            etEmoji.setText(args.getString(ARG_EMOJI, "📝"))
            etTitle.setText(args.getString(ARG_TITLE, ""))
            etDescription.setText(args.getString(ARG_DESCRIPTION, ""))
            etTime.setText(args.getString(ARG_TIME, ""))
        }

        etTime.setOnClickListener {
            val parts = etTime.text.toString().split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 12
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            TimePickerDialog(requireContext(), { _, h, m ->
                etTime.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m))
            }, hour, minute, true).show()
        }

        btnCancel.setOnClickListener { dismiss() }

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

            val id = editingId ?: System.currentTimeMillis()
            val item = TimelineItem(
                id = id,
                emoji = emoji,
                title = title,
                description = description,
                time = time
            )

            onSaved?.invoke(item)
            dismiss()
        }
    }
}