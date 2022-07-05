package com.example.criminalintentapp.presentation.authentication

import android.renderscript.Sampler
import android.util.Log
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthServiceImpl(): FirebaseAuthService {
    override fun login(email: String, password: String): Result<String,String> {
        Log.d("Service", "Login success")
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                return@addOnSuccessListener Result.Success<>
            }
            .addOnFailureListener {

            }
    }
}