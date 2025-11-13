package com.intern002.locketapp.ui.screen.auth.welcome

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.NumberPicker
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.intern002.locketapp.R

class NumberPickerDialogFragment : DialogFragment() {

    interface NumberPickerListener {
        fun onNumberSelected(tag: String, value: Int)
    }

    private var listener: NumberPickerListener? = null
    private var title: String = "Select Value"
    private var minValue: Int = 1
    private var maxValue: Int = 31
    private var currentValue: Int = 1

    companion object {
        const val TAG_MONTH = "month_picker"
        const val TAG_DAY = "day_picker"

        fun newInstance(tag: String, title: String, min: Int, max: Int, current: Int): NumberPickerDialogFragment {
            val fragment = NumberPickerDialogFragment()
            val args = Bundle().apply {
                putString("tag", tag)
                putString("title", title)
                putInt("min", min)
                putInt("max", max)
                putInt("current", current)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            title = it.getString("title", "Select Value")
            minValue = it.getInt("min", 1)
            maxValue = it.getInt("max", 31)
            currentValue = it.getInt("current", 1)
        }

        listener = parentFragment as? NumberPickerListener
            ?: (activity as? NumberPickerListener)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_number_picker, null)
        val numberPicker: NumberPicker = view.findViewById(R.id.number_picker)
        val titleTextView: TextView = view.findViewById(R.id.dialog_title)
        val okButton: Button = view.findViewById(R.id.btn_ok)

        titleTextView.text = title
        numberPicker.minValue = minValue
        numberPicker.maxValue = maxValue
        numberPicker.value = currentValue

        val dialog = AlertDialog.Builder(requireContext())
            .setView(view)
            .create()

        okButton.setOnClickListener {
            val selectedValue = numberPicker.value
            listener?.onNumberSelected(tag!!, selectedValue)
            dismiss()
        }

        return dialog
    }
}