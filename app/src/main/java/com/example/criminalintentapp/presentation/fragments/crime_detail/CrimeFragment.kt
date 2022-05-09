package com.example.criminalintentapp.presentation.fragments.crime_detail

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.presentation.dialogs.DatePickerFragment
import com.example.criminalintentapp.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class CrimeFragment : Fragment(R.layout.fragment_crime), FragmentResultListener {

    private var crime: Crime = Crime()
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var saveButton: Button
    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onStart() {
        super.onStart()
        val crimeId: Int = arguments?.getSerializable(ARG_CRIME_ID) as Int
        crimeDetailViewModel.loadCrime(crimeId)
        if (crimeDetailViewModel.crimeLiveData.value == null) {
            dateButton.text = crime.date.toString()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showAlertDialog()
                }
            })

        bindViews(view)
        setTextWatcher()
        setClickListeners()

        childFragmentManager.setFragmentResultListener(
            REQUEST_DATE,
            viewLifecycleOwner,
            this
        )
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner
        ) { crime ->
            crime?.let {
                this.crime = crime
                updateUI()
            }
        }
    }

    private fun setTextWatcher() {
        val titleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // not implemented
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // not implemented
            }
        }

        titleField.addTextChangedListener(titleWatcher)
    }

    private fun setClickListeners() {
        solvedCheckBox.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crime.date, REQUEST_DATE).apply {
                show(this@CrimeFragment.childFragmentManager, REQUEST_DATE)
            }
        }

        saveButton.setOnClickListener {
            if (crimeDetailViewModel.crimeLiveData.value == null) {
                crimeDetailViewModel.addCrime(crime)
                activity?.supportFragmentManager?.popBackStack()
            } else {
                crimeDetailViewModel.saveCrime(crime)
                activity?.supportFragmentManager?.popBackStack()
            }
        }
    }

    private fun updateUI() {
        titleField.setText(crime.title)
        dateButton.apply {
            text = crime.date.toString()
        }
        solvedCheckBox.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState()
        }
    }

    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when (requestCode) {
            REQUEST_DATE -> {
                Log.d(TAG, "RECEIVED RESULT FOR $requestCode")
                crime.date = DatePickerFragment.getSelectedDate(result)
            }
        }
    }

    private fun bindViews(view: View) {
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        saveButton = view.findViewById(R.id.crime_save) as Button
    }

    private fun showAlertDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle("Alert")
                .setMessage("If you exit now, the changes will not be saved")
                .setNegativeButton(
                    "Discard changes"
                ) { _, _ -> activity?.supportFragmentManager?.popBackStack() }
                .setPositiveButton(
                    "Continue editing"
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
        }
    }

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val REQUEST_DATE = "DialogDate"
        private const val TAG = "CrimeFragment"

        fun newInstance(crimeId: Int): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }
}
