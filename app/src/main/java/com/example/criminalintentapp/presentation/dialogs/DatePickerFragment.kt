package com.example.criminalintentapp.presentation.dialogs

import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class DatePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dateListener =
            DatePickerDialog.OnDateSetListener { _: DatePicker, year: Int, month: Int, day: Int ->

                val resultDate: Date = GregorianCalendar(year, month, day).time

                val selectedDate = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH).format(resultDate)

                val result = Bundle().apply {
                    putString(RESULT_DATE_KEY, selectedDate)
                }

                val resultRequestCode = requireArguments().getString(ARG_REQUEST_CODE, "")
                parentFragmentManager.setFragmentResult(resultRequestCode, result)
            }

        val calendar = Calendar.getInstance()
        val initialYear = calendar.get(Calendar.YEAR)
        val initialMonth = calendar.get(Calendar.MONTH)
        val initialDay = calendar.get(Calendar.DAY_OF_MONTH)

        return DatePickerDialog(
            requireContext(),
            dateListener,
            initialYear,
            initialMonth,
            initialDay
        )
    }

    companion object {
        private const val ARG_REQUEST_CODE = "requestCode"
        private const val RESULT_DATE_KEY = "dateKey"

        fun newInstance(requestCode: String): DatePickerFragment {
            val args = Bundle().apply {
                putString(ARG_REQUEST_CODE, requestCode)
            }

            return DatePickerFragment().apply {
                arguments = args
            }
        }

        fun getSelectedDate(result: Bundle) = result.getSerializable(RESULT_DATE_KEY)
    }
}
