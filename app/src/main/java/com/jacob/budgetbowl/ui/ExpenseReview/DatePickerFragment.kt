package com.jacob.budgetbowl.ui.ExpenseReview

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import com.jacob.budgetbowl.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DatePickerFragment : DialogFragment() {

    private lateinit var requestKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            requestKey = it.getString(ARG_REQUEST_KEY)!!
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val year = c.get(Calendar.YEAR)
        val month = c.get(Calendar.MONTH)
        val day = c.get(Calendar.DAY_OF_MONTH)

        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            val calendar = Calendar.getInstance()
            calendar.set(year, month, dayOfMonth)
            val simpleDateFormat = SimpleDateFormat("EEE, d MMM", Locale.getDefault())
            val selectedDate = simpleDateFormat.format(calendar.time)

            parentFragmentManager.setFragmentResult(requestKey, Bundle().apply {
                putString("selectedDate", selectedDate)
            })
        }

        val dialog = DatePickerDialog(requireActivity(), R.style.MyDatePickerDialogTheme, dateSetListener, year, month, day)
        dialog.setButton(DatePickerDialog.BUTTON_POSITIVE, "Confirm", dialog)
        dialog.setButton(DatePickerDialog.BUTTON_NEGATIVE, "Cancel", dialog)
        return dialog
    }

    companion object {
        private const val ARG_REQUEST_KEY = "request_key"

        fun newInstance(requestKey: String): DatePickerFragment {
            val fragment = DatePickerFragment()
            val args = Bundle()
            args.putString(ARG_REQUEST_KEY, requestKey)
            fragment.arguments = args
            return fragment
        }
    }
}
