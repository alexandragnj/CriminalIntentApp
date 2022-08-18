package com.example.criminalintentapp.presentation.fragments.crime_detail

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentResultListener
import androidx.navigation.fragment.NavHostFragment
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.FragmentCrimeBinding
import com.example.criminalintentapp.presentation.dialogs.DatePickerFragment
import com.example.criminalintentapp.presentation.dialogs.TimePickerFragment
import com.example.criminalintentapp.utils.getScaledBitmap
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CrimeFragment : Fragment(R.layout.fragment_crime), FragmentResultListener {

    private lateinit var temporaryFile: File
    private lateinit var temporaryUri: Uri
    private lateinit var binding: FragmentCrimeBinding

    val crimeDetailViewModel: CrimeDetailViewModel by viewModel()

    private val resultLauncherSuspect =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResultSuspect(result)
        }

    private val resultLauncherPhoto =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            onActivityResultPhoto(result)
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCrimeBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.show()
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showAlertDialog()
                }
            }
        )

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
        childFragmentManager.setFragmentResultListener(
            REQUEST_TIME,
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

        val crimeId: Long = arguments?.getSerializable(ARG_CRIME_ID) as Long
        crimeDetailViewModel.loadCrime(crimeId)

        Log.d(TAG, "onViewCreated")
        Log.d(TAG, "Id cretE: ${crimeId}")
    }

    override fun onFragmentResult(requestCode: String, result: Bundle) {
        when (requestCode) {
            REQUEST_DATE -> {
                Log.d(TAG, "RECEIVED RESULT FOR $requestCode")
                crimeDetailViewModel.crime.date =
                    DatePickerFragment.getSelectedDate(result) as String
            }
            REQUEST_TIME -> {
                Log.d(TAG, "RECEIVED RESULT FOR $requestCode")
                crimeDetailViewModel.crime.time =
                    TimePickerFragment.getSelectedTime(result) as String
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

        binding.crimeTitle.addTextChangedListener(titleWatcher)
    }

    private fun setClickListeners() {
        binding.crimeSolved.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crimeDetailViewModel.crime.isSolved = isChecked
            }
        }

        binding.crimeDate.setOnClickListener {
            DatePickerFragment.newInstance(REQUEST_DATE).apply {
                show(this@CrimeFragment.childFragmentManager, REQUEST_DATE)
            }
        }

        binding.crimeTime.setOnClickListener {
            TimePickerFragment.newInstance(REQUEST_TIME).apply {
                show(this@CrimeFragment.childFragmentManager, REQUEST_TIME)
            }
        }

        binding.crimeSave.setOnClickListener {
            temporaryFile.renameTo(
                File(
                    context?.applicationContext?.filesDir,
                    crimeDetailViewModel.crime.photoFileName
                )
            )

            if (crimeDetailViewModel.createOrModifyCrime()) {
                NavHostFragment.findNavController(this@CrimeFragment)
                    .navigate(R.id.action_crimeFragment_to_crimeListFragment)
            } else {
                updateFirestore()

                NavHostFragment.findNavController(this@CrimeFragment)
                    .navigate(R.id.action_crimeFragment_to_crimeListFragment)
            }

            Log.d(TAG, "suspect: ${crimeDetailViewModel.crime.suspect}")
        }

        binding.crimeDelete.setOnClickListener {
            val crimeId: Long = arguments?.getSerializable(ARG_CRIME_ID) as Long
            crimeDetailViewModel.deleteCrime(crimeId)
            //FirestoreClass().deleteCrime(crimeDetailViewModel.crime)
            crimeDetailViewModel.deleteFirestore()
            NavHostFragment.findNavController(this@CrimeFragment)
                .navigate(R.id.action_crimeFragment_to_crimeListFragment)
        }

        binding.crimeReport.setOnClickListener {
            sendReport()
        }

        binding.crimeSuspect.setOnClickListener {
            setIntentSuspect()
        }

        binding.crimeCamera.setOnClickListener {
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

    private fun updateFirestore() {
        val crimeHashMap = HashMap<String, Any>()

        when {
            binding.crimeTitle.text.toString() != crimeDetailViewModel.crime.title -> crimeHashMap["title"] =
                binding.crimeTitle.text.toString()
            binding.crimeDate.text.toString() != crimeDetailViewModel.crime.date -> crimeHashMap["date"] =
                binding.crimeDate.text.toString()
            binding.crimeTime.text.toString() != crimeDetailViewModel.crime.time -> crimeHashMap["time"] =
                binding.crimeTime.text.toString()
            binding.crimeSuspect.text.toString() != crimeDetailViewModel.crime.suspect -> crimeHashMap["suspect"] =
                binding.crimeSuspect.text.toString()
        }

        crimeDetailViewModel.updateFirestore(crimeHashMap)
    }

    private fun updateUI() {
        binding.crimeTitle.setText(crimeDetailViewModel.crime.title)
        binding.crimeDate.text = crimeDetailViewModel.crime.date
        if (crimeDetailViewModel.crime.date.isEmpty()) {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
            val date = sdf.format(Date())
            binding.crimeDate.text = date
            crimeDetailViewModel.crime.date = date
        }
        binding.crimeTime.text = crimeDetailViewModel.crime.time
        if (crimeDetailViewModel.crime.time.isEmpty()) {
            val sdf = SimpleDateFormat("h:mm a", Locale.ENGLISH)
            val date = sdf.format(Date())
            binding.crimeTime.text = date
            crimeDetailViewModel.crime.time = date
        }
        binding.crimeSolved.apply {
            isChecked = crimeDetailViewModel.crime.isSolved
            jumpDrawablesToCurrentState()
        }
        if (crimeDetailViewModel.crime.suspect.isNotEmpty()) {
            binding.crimeSuspect.text = crimeDetailViewModel.crime.suspect
        }

        val photoFile =
            File(context?.applicationContext?.filesDir, crimeDetailViewModel.crime.photoFileName)
        updatePhotoView(photoFile)
    }

    private fun updatePhotoView(photoFile: File) {
        var bitmap: Bitmap? = null
        when {
            photoFile.exists() -> bitmap = getScaledBitmap(photoFile.path, requireActivity())
            temporaryFile.exists() -> bitmap =
                getScaledBitmap(temporaryFile.path, requireActivity())
        }
        updatePhotoViewProperties(bitmap)
    }

    private fun updatePhotoViewProperties(bitmap: Bitmap?) {
        binding.crimePhoto.setImageBitmap(bitmap)
        binding.crimePhoto.contentDescription = getString(R.string.crime_photo_image_description)
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crimeDetailViewModel.crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }

        //val dateString = DateFormat.format(DATE_FORMAT, crimeDetailViewModel.crime.date).toString()
        val dateString =
            SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH).format(crimeDetailViewModel.crime.date)
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
        binding.crimeSuspect.text = suspect
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

    private fun showAlertDialog() {
        context?.let {
            MaterialAlertDialogBuilder(it)
                .setTitle(R.string.dialog_title)
                .setMessage(R.string.dialog_message)
                .setNegativeButton(
                    R.string.dialog_negative_button
                ) { _, _ ->
                    temporaryFile.delete()
                    NavHostFragment.findNavController(this)
                        .navigate(R.id.action_crimeFragment_to_crimeListFragment)
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
        private const val REQUEST_TIME = "DialogTime"
        private const val TAG = "CrimeFragment"
        private const val DATE_FORMAT = "EEE, MMM, dd"
        private const val AUTHORITY = "com.example.criminalintentapp.fileprovider"
    }
}
