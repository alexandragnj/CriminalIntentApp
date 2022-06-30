package com.example.criminalintentapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.criminalintentapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private val authenticationViewModel: AuthenticationViewModel by lazy {
        ViewModelProvider(this).get(AuthenticationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        authenticationViewModel.userLoginLiveData.observe(this) { user ->
            if (user != null) {
                Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()

                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }

        authenticationViewModel.failureLiveData.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_LONG)
                .show()
        }

        setOnClickListeners(binding)
    }

    private fun setOnClickListeners(binding: ActivityLoginBinding) {
        binding.tvSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        binding.btnSignIn.setOnClickListener {
            if (binding.etSignInEmail.text.isEmpty() && binding.etSignInPassword.text.isEmpty()) {
                Toast.makeText(this, "Complete the Email and Password fields", Toast.LENGTH_LONG)
                    .show()
            } else {
                authenticationViewModel.login(
                    binding.etSignInEmail.text.toString(),
                    binding.etSignInPassword.text.toString()
                )
            }
        }
    }
}