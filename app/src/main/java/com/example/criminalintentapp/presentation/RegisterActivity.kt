package com.example.criminalintentapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.criminalintentapp.R
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {

    private lateinit var emailSignUp: EditText
    private lateinit var passwordSignUp: EditText
    private lateinit var signUpButton: Button
    private lateinit var signInTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        bindViews()

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        signInTextView.setOnClickListener {
            onBackPressed()
        }

        signUpButton.setOnClickListener {
            if (emailSignUp.text.isEmpty() && passwordSignUp.text.isEmpty()) {
                Toast.makeText(this, "Complete the Email and Password fields", Toast.LENGTH_LONG)
                    .show()
            } else {
                registerUser()
            }
        }
    }

    private fun registerUser() {

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            emailSignUp.text.toString(),
            passwordSignUp.text.toString()
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
                    onBackPressed()
                } else {
                    Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun bindViews() {
        emailSignUp = findViewById(R.id.edit_text_signUp_email)
        passwordSignUp = findViewById(R.id.edit_text_signUp_password)
        signUpButton = findViewById(R.id.button_signUp)
        signInTextView = findViewById(R.id.text_view_signIn)
    }
}