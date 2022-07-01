package com.example.criminalintentapp.presentation.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.ActivityRegisterBinding
import com.example.criminalintentapp.presentation.MainActivity

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
            user?.let {
                Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                goToMainActivity()
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
            tryToRegister(binding)
        }
    }

    private fun tryToRegister(binding: ActivityRegisterBinding) {
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

    private fun goToMainActivity() {
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}