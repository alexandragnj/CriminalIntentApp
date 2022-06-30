package com.example.criminalintentapp.presentation.authentication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private val authenticationViewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(AuthenticationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModelObservers()

        setOnClickListeners(binding)
    }

    private fun initViewModelObservers() {
        authenticationViewModel.userRegisterLiveData.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                onBackPressed()
            }
        }

        authenticationViewModel.failureLiveData.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setOnClickListeners(binding: ActivityRegisterBinding) {
        binding.tvSignIn.setOnClickListener {
            onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            if (authenticationViewModel.checkFields(
                    binding.etSignUpEmail.text.toString(),
                    binding.etSignUpPassword.text.toString()
                )
            ) {
                authenticationViewModel.register(
                    binding.etSignUpEmail.text.toString(),
                    binding.etSignUpPassword.text.toString()
                )
            } else {
                Toast.makeText(this, getString(R.string.empty_fields), Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
}