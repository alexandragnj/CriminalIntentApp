package com.example.criminalintentapp.presentation.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.ActivityLoginBinding
import com.example.criminalintentapp.presentation.MainActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {

    private val authenticationViewModel: AuthenticationViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (authenticationViewModel.currentUser != null) {
            goToMainActivity(false)
        }

        initViewModelObservers()
        setOnClickListeners(binding)
    }

    private fun initViewModelObservers() {
        authenticationViewModel.userLoginLiveData.observe(this) { user ->
            user?.let {
                goToMainActivity(true)
            }
        }

        authenticationViewModel.failureLiveData.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun setOnClickListeners(binding: ActivityLoginBinding) {
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            tryToLogin(binding)
        }
    }

    private fun tryToLogin(binding: ActivityLoginBinding) {
        val email = binding.etSignInEmail.text.toString()
        val password = binding.etSignInPassword.text.toString()

        if (authenticationViewModel.checkFields(email, password)) {
            authenticationViewModel.login(email, password)
        } else {
            Toast.makeText(this, getString(R.string.empty_fields), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun goToMainActivity(clearBackStack: Boolean) {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        if (clearBackStack) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}