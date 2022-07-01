package com.example.criminalintentapp.presentation.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintentapp.R
import com.example.criminalintentapp.databinding.ActivityLoginBinding
import com.example.criminalintentapp.presentation.MainActivity

class LoginActivity : AppCompatActivity() {

    private val authenticationViewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(AuthenticationViewModel::class.java)
    }

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
            tryToLoginIn(binding)
        }
    }

    private fun tryToLoginIn(binding: ActivityLoginBinding) {
        if (authenticationViewModel.checkFields(
                binding.etSignInEmail.text.toString(),
                binding.etSignInPassword.text.toString()
            )
        ) {
            authenticationViewModel.login(
                binding.etSignInEmail.text.toString(),
                binding.etSignInPassword.text.toString()
            )
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