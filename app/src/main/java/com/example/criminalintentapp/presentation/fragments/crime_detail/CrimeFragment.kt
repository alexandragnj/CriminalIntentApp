package com.example.criminalintentapp.presentation.fragments.crime_detail

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResult(result)
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

    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when (requestCode) {
            REQUEST_DATE -> {
                Log.d(TAG, "RECEIVED RESULT FOR $requestCode")
                crime.date = DatePickerFragment.getSelectedDate(result)
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

        reportButton.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject)
                )
            }.also { intent ->
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        suspectButton.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                resultLauncher.launch(pickContactIntent)
            }
        }
    }

    private fun onActivityResult(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "There is no result - onActiviyResult")
            return
        } else {
            val contactUri: Uri? = result.data?.data
            //Specify which fields you want your query to return values for
            val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
            //Perform your query - the contactUri is like a "where" clause here
            val cursor = contactUri?.let {
                requireActivity().contentResolver
                    .query(it, queryFields, null, null, null)
            }
            cursor?.use {
                //Verify cursor contains at least one result
                if (it.count == 0) {
                    return
                }

                //Pull out the first column of the first row of data
                //that is your suspect's name
                it.moveToFirst()
                val suspect = it.getString(0)
                crime.suspect = suspect
                crimeDetailViewModel.saveCrime(crime)
                suspectButton.text = suspect
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
        if (crime.suspect.isNotEmpty()) {
            suspectButton.text = crime.suspect
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crime.date).toString()
        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }

    private fun bindViews(view: View) {
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        saveButton = view.findViewById(R.id.crime_save) as Button
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
    }

    private fun showAlertDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setNegativeButton(
                    R.string.dialog_negative_button
                ) { _, _ -> activity?.supportFragmentManager?.popBackStack() }
                .setPositiveButton(
                    R.string.dialog_positive_button
                ) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
        }
    }

    companion object {
        private const val ARG_CRIME_ID = "crime_id"
        private const val REQUEST_DATE = "DialogDate"
        private const val TAG = "CrimeFragment"
        private const val DATE_FORMAT = "EEE, MMM, dd"

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
