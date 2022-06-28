package com.example.criminalintentapp.presentation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.example.criminalintentapp.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var emailSignIn: EditText
    private lateinit var passwordSignIn: EditText
    private lateinit var signInButton: Button
    private lateinit var signUpTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val user=FirebaseAuth.getInstance().currentUser
        if(user!=null){
            val intent=Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        bindViews()

        setOnClickListeners()
    }

    private fun setOnClickListeners(){
        signUpTextView.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        signInButton.setOnClickListener {
            if (emailSignIn.text.isEmpty() && passwordSignIn.text.isEmpty()) {
                Toast.makeText(this, "Complete the Email and Password fields", Toast.LENGTH_LONG)
                    .show()
            } else {
                loginUser()
            }
        }
    }

    private fun loginUser() {
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(emailSignIn.text.toString(), passwordSignIn.text.toString())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Logged in successfully", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@LoginActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else{
                    Toast.makeText(this, task.exception!!.message.toString(), Toast.LENGTH_LONG)
                        .show()
                }
            }
    }

    private fun bindViews() {
        emailSignIn = findViewById(R.id.edit_text_signIn_email)
        passwordSignIn = findViewById(R.id.edit_text_signIn_password)
        signInButton = findViewById(R.id.button_signIn)
        signUpTextView = findViewById(R.id.text_view_signUp)
    }
}