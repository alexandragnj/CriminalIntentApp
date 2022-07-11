package com.example.criminalintentapp.presentation.authentication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.NavHostFragment
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.FragmentRegisterBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class RegisterFragment : Fragment() {

    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding= FragmentRegisterBinding.inflate(inflater,container,false)

        initViewModelObservers()
        setOnClickListeners(binding)

        return binding.root
    }

    private fun initViewModelObservers() {
        authenticationViewModel.userRegisterLiveData.observe(viewLifecycleOwner) { user ->
            user?.let {
                goToCrimeList()
            }
        }

        authenticationViewModel.failureLiveData.observe(viewLifecycleOwner) { message ->
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setOnClickListeners(binding: FragmentRegisterBinding) {
        binding.tvSignIn.setOnClickListener {
            activity?.onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            tryToRegister(binding)
        }
    }

    private fun tryToRegister(binding: FragmentRegisterBinding) {
        val email = binding.etSignUpEmail.text.toString()
        val password = binding.etSignUpPassword.text.toString()

        if (authenticationViewModel.checkFields(email, password)) {
            authenticationViewModel.register(email, password)
        } else {
            Toast.makeText(requireContext(), getString(R.string.empty_fields), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goToCrimeList() {
        NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_crimeListFragment)
    }
}