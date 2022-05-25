package com.example.criminalintentapp.presentation.fragments.crime_detail

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintentapp.R
import com.example.criminalintentapp.getScaledBitmap
import com.example.criminalintentapp.presentation.dialogs.DatePickerFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class CrimeFragment : Fragment(R.layout.fragment_crime), FragmentResultListener {

    private lateinit var temporaryFile: File
    private lateinit var temporaryUri: Uri
    private lateinit var titleField: EditText
    private lateinit var dateButton: Button
    private lateinit var solvedCheckBox: CheckBox
    private lateinit var saveButton: Button
    private lateinit var reportButton: Button
    private lateinit var suspectButton: Button
    private lateinit var photoButton: ImageButton
    private lateinit var photoView: ImageView

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProvider(this).get(CrimeDetailViewModel::class.java)
    }

    private val resultLauncherSuspect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResultSuspect(result)
        }

    private val resultLauncherPhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResultPhoto(result)
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showAlertDialog()
                }
            }
        )

        bindViews(view)
        setTextWatcher()

        temporaryFile = File(context?.applicationContext?.filesDir, "temporary_file")
        temporaryUri = FileProvider.getUriForFile(
            requireActivity(),
            AUTHORITY,
            temporaryFile
        )

        childFragmentManager.setFragmentResultListener(
            REQUEST_DATE,
            viewLifecycleOwner,
            this
        )
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner
        ) { crime ->
            crime?.let {
                crimeDetailViewModel.crime = crime
            }
            updateUI()
        }

        setClickListeners()

        val crimeId: Int = arguments?.getSerializable(ARG_CRIME_ID) as Int
        crimeDetailViewModel.loadCrime(crimeId)
    }

    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when (requestCode) {
            REQUEST_DATE -> {
                Log.d(TAG, "RECEIVED RESULT FOR $requestCode")
                crimeDetailViewModel.crime.date = DatePickerFragment.getSelectedDate(result)
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
                crimeDetailViewModel.crime.title = sequence.toString()
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
                crimeDetailViewModel.crime.isSolved = isChecked
            }
        }

        dateButton.setOnClickListener {
            DatePickerFragment.newInstance(crimeDetailViewModel.crime.date, REQUEST_DATE).apply {
                show(this@CrimeFragment.childFragmentManager, REQUEST_DATE)
            }
        }

        saveButton.setOnClickListener {
            temporaryFile.renameTo(
                File(
                    context?.applicationContext?.filesDir,
                    crimeDetailViewModel.crime.photoFileName
                )
            )

            if (crimeDetailViewModel.crimeLiveData.value == null) {
                crimeDetailViewModel.addCrime(crimeDetailViewModel.crime)
                activity?.supportFragmentManager?.popBackStack()
            } else {
                crimeDetailViewModel.saveCrime(crimeDetailViewModel.crime)
                activity?.supportFragmentManager?.popBackStack()
            }
            Log.d(TAG, "suspect: ${crimeDetailViewModel.crime.suspect}")
        }

        reportButton.setOnClickListener {
            sendReport()
        }

        suspectButton.setOnClickListener {
            setIntentSuspect()
        }

        photoButton.setOnClickListener {
            takePhoto()
        }
    }

    private fun onActivityResultSuspect(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "There is no result - onActivityResult")
            return
        } else {
            getSuspectName(result.data?.data)
            Log.d(TAG, "merge")
        }
    }

    private fun onActivityResultPhoto(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "There is no result - onActivityResult")
            return
        } else {
            requireActivity().revokeUriPermission(
                temporaryUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
            updatePhotoView(temporaryFile)
        }
    }

    private fun updateUI() {
        titleField.setText(crimeDetailViewModel.crime.title)
        dateButton.text = crimeDetailViewModel.crime.date.toString()
        solvedCheckBox.apply {
            isChecked = crimeDetailViewModel.crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crimeDetailViewModel.crime.suspect.isNotEmpty()) {
            suspectButton.text = crimeDetailViewModel.crime.suspect
        }

        val photoFile =
            File(context?.applicationContext?.filesDir, crimeDetailViewModel.crime.photoFileName)
        updatePhotoView(photoFile)
    }

    private fun updatePhotoView(photoFile: File?) {
        if (photoFile != null && photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            photoView.setImageBitmap(bitmap)
            photoView.contentDescription = getString(R.string.crime_photo_image_description)
        } else {
            photoView.setImageDrawable(null)
            photoView.contentDescription = getString(R.string.crime_photo_no_image_description)
        }
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crimeDetailViewModel.crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        val dateString = DateFormat.format(DATE_FORMAT, crimeDetailViewModel.crime.date).toString()
        val suspect = if (crimeDetailViewModel.crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crimeDetailViewModel.crime.suspect)
        }

        return getString(
            R.string.crime_report,
            crimeDetailViewModel.crime.title,
            dateString,
            solvedString,
            suspect
        )
    }

    private fun sendReport() {
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

    private fun getSuspectName(data: Uri?) {
        val contactUri: Uri? = data
        // Specify which fields you want your query to return values for
        val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
        // Perform your query - the contactUri is like a "where" clause here
        val cursor = contactUri?.let {
            requireActivity().contentResolver
                .query(it, queryFields, null, null, null)
        }

        setSuspectName(cursor!!)
    }

    private fun setSuspectName(cursor: Cursor) {
        if (cursor.count == 0) {
            return
        }

        // Pull out the first column of the first row of data
        // that is your suspect's name
        cursor.moveToFirst()
        val suspect = cursor.getString(0)
        crimeDetailViewModel.crime.suspect = suspect
        suspectButton.text = suspect
    }

    private fun setIntentSuspect() {
        val pickContactIntent =
            Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

        resultLauncherSuspect.launch(pickContactIntent)
    }

    private fun takePhoto() {
        val packageManager: PackageManager = requireActivity().packageManager
        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        captureImage.putExtra(MediaStore.EXTRA_OUTPUT, temporaryUri)

        val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(
            captureImage,
            PackageManager.MATCH_DEFAULT_ONLY
        )
        for (cameraActivity in cameraActivities) {
            requireActivity().grantUriPermission(
                cameraActivity.activityInfo.packageName,
                temporaryUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
        resultLauncherPhoto.launch(captureImage)
    }

    private fun bindViews(view: View) {
        titleField = view.findViewById(R.id.crime_title) as EditText
        dateButton = view.findViewById(R.id.crime_date) as Button
        solvedCheckBox = view.findViewById(R.id.crime_solved) as CheckBox
        saveButton = view.findViewById(R.id.crime_save) as Button
        reportButton = view.findViewById(R.id.crime_report) as Button
        suspectButton = view.findViewById(R.id.crime_suspect) as Button
        photoButton = view.findViewById(R.id.crime_camera) as ImageButton
        photoView = view.findViewById(R.id.crime_photo) as ImageView
    }

    private fun showAlertDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setNegativeButton(
                    R.string.dialog_negative_button
                ) { _, _ ->
                    temporaryFile.delete()
                    activity?.supportFragmentManager?.popBackStack()
                }
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
        private const val AUTHORITY = "com.example.criminalintentapp.fileprovider"

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
