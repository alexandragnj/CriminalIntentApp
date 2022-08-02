package com.example.criminalintentapp.presentation.dialogs

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.text.SimpleDateFormat
import java.util.*

class TimePickerFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val timeListener =
            TimePickerDialog.OnTimeSetListener { _: TimePicker, hour: Int, minute: Int ->

                val resultTime: Date = GregorianCalendar(0, 0, 0, hour, minute).time

                val selectedDate = SimpleDateFormat("h:mm a", Locale.ENGLISH).format(resultTime)

                val result = Bundle().apply {
                    putString(RESULT_TIME_KEY, selectedDate)
                }

                val resultRequestCode = requireArguments().getString(ARG_REQUEST_CODE, "")
                parentFragmentManager.setFragmentResult(resultRequestCode, result)
            }

        val calendar = Calendar.getInstance()
        val initialHour = calendar.get(Calendar.HOUR)
        val initialMinute = calendar.get(Calendar.MINUTE)

        return TimePickerDialog(
            requireContext(),
            timeListener,
            initialHour,
            initialMinute,
            true
        )
    }

    companion object {
        private const val ARG_REQUEST_CODE = "requestCode"
        private const val RESULT_TIME_KEY = "timeKey"

        fun newInstance(requestCode: String): TimePickerFragment {
            val args = Bundle().apply {
                putString(ARG_REQUEST_CODE, requestCode)
            }

            return TimePickerFragment().apply {
                arguments = args
            }
        }

        fun getSelectedTime(result: Bundle) = result.getSerializable(RESULT_TIME_KEY)
    }
}