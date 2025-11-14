
package com.jacob.budgetbowl.ui.ExpenseReview

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import java.util.Calendar

class DatePickerFragment : DialogFragment(), DatePickerDialog.OnDateSetListener {

    private var editTextId: Int = -1

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        arguments?.let {
            editTextId = it.getInt(ARG_EDIT_TEXT_ID)
        }

        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(requireActivity(), this, year, month, day)
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val selectedDate = "$dayOfMonth/${month + 1}/$year"
        val editText = activity?.findViewById<EditText>(editTextId)
        editText?.setText(selectedDate)
    }

    companion object {
        private const val ARG_EDIT_TEXT_ID = "edit_text_id"

        fun newInstance(editTextId: Int): DatePickerFragment {
            val fragment = DatePickerFragment()
            val args = Bundle()
            args.putInt(ARG_EDIT_TEXT_ID, editTextId)
            fragment.arguments = args
            return fragment
        }
    }
}
