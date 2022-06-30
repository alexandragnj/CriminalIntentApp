package com.example.criminalintentapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintentapp.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private val authenticationViewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(AuthenticationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        setOnClickListeners(binding)
    }

    private fun setOnClickListeners(binding: ActivityRegisterBinding) {
        binding.tvSignIn.setOnClickListener {
            onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            if (binding.etSignUpEmail.text.isEmpty() && binding.etSignUpPassword.text.isEmpty()) {
                Toast.makeText(this, "Complete the Email and Password fields", Toast.LENGTH_LONG)
                    .show()
            } else {
                authenticationViewModel.register(
                    binding.etSignUpEmail.text.toString(),
                    binding.etSignUpPassword.text.toString()
                )
            }
        }
    }
}