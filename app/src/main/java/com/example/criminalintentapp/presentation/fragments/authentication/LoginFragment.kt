package com.example.criminalintentapp.presentation.fragments.authentication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.FragmentLoginBinding
import com.example.criminalintentapp.presentation.dialogs.ProgressDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var googleSignInClient: GoogleSignInClient
    private val authenticationViewModel: AuthenticationViewModel by viewModel()
    private lateinit var progressDialog: ProgressDialog

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        authenticationViewModel.callbackManager.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            authenticationViewModel.googleLogin(data)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()

        progressDialog = ProgressDialog(requireActivity())

        if (authenticationViewModel.currentUser != null) {
            goToCrimeList()
        }

        setupGoogleLogin()
        initViewModelObservers()
        setOnClickListeners()
    }

    private fun setupFacebookLogin() {
        binding.btnFacebook.setReadPermissions("email", "public_profile", "user_friends")
        binding.btnFacebook.registerCallback(
            authenticationViewModel.callbackManager,
            authenticationViewModel.facebookLogin()
        )

    }

    private fun setupGoogleLogin() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
    }

    private fun signInWithGoogle() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_SIGN_IN)

    }

    private fun initViewModelObservers() {
        authenticationViewModel.userLoginLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                goToCrimeList()
            }
        }

        authenticationViewModel.failureLiveData.observe(viewLifecycleOwner) { message ->
            progressDialog.hide()
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setOnClickListeners() {
        binding.tvSignUp.setOnClickListener {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnSignIn.setOnClickListener {
            tryToLogin()
        }

        binding.btnFacebook.setOnClickListener {
            setupFacebookLogin()
        }

        binding.btnGoogle.setOnClickListener {
            signInWithGoogle()
        }
    }

    private fun tryToLogin() {
        val email = binding.etSignInEmail.text.toString()
        val password = binding.etSignInPassword.text.toString()

        if (authenticationViewModel.checkFields(email, password)) {
            progressDialog.show()
            authenticationViewModel.login(email, password)
        } else {
            Toast.makeText(requireContext(), getString(R.string.empty_fields), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goToCrimeList() {
        progressDialog.hide()
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_loginFragment_to_crimeListFragment)
    }

    companion object {
        const val TAG = "LoginFragment"
        private const val REQUEST_CODE_GOOGLE_SIGN_IN = 120
    }
}