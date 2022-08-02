package com.example.criminalintentapp.presentation.fragments.authentication

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.FragmentRegisterBinding
import com.example.criminalintentapp.presentation.dialogs.ProgressDialog
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        progressDialog = ProgressDialog(requireActivity())

        initViewModelObservers()
        setOnClickListeners()
    }

    private fun initViewModelObservers() {
        authenticationViewModel.userRegisterLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                goToCrimeList()
            }
        }

        authenticationViewModel.failureLiveData.observe(viewLifecycleOwner) { message ->
            progressDialog.hideProgressDialog()
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setOnClickListeners() {
        binding.tvSignIn.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            tryToRegister()
        }
    }

    fun checkEmail(email: String) {
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.result.signInMethods?.size != 0) {
                    Toast.makeText(requireContext(), "Email already exists.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }

    private fun tryToRegister() {
        val email = binding.etSignUpEmail.text.toString()
        val password = binding.etSignUpPassword.text.toString()

        if (authenticationViewModel.checkFields(email, password)) {
            progressDialog.showProgressDialog()
            //checkEmail(email)
            authenticationViewModel.register(email, password)
        } else {
            Toast.makeText(requireContext(), getString(R.string.empty_fields), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goToCrimeList() {
        progressDialog.hideProgressDialog()
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_registerFragment_to_crimeListFragment)
    }
}