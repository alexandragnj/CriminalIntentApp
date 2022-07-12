package com.example.criminalintentapp.presentation.fragments.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.FragmentLoginBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (authenticationViewModel.currentUser != null) {
            goToCrimeList()
        }

        initViewModelObservers()
        setOnClickListeners()
    }

    private fun initViewModelObservers() {
        authenticationViewModel.userLoginLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                goToCrimeList()
            }
        }

        authenticationViewModel.failureLiveData.observe(viewLifecycleOwner) { message ->
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
    }

    private fun tryToLogin() {
        val email = binding.etSignInEmail.text.toString()
        val password = binding.etSignInPassword.text.toString()

        if (authenticationViewModel.checkFields(email, password)) {
            authenticationViewModel.login(email, password)
        } else {
            Toast.makeText(requireContext(), getString(R.string.empty_fields), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goToCrimeList() {
        NavHostFragment.findNavController(this)
            .navigate(R.id.action_loginFragment_to_crimeListFragment)
    }
}