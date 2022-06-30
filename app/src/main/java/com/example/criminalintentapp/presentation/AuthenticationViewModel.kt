package com.example.criminalintentapp.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class AuthenticationViewModel : ViewModel() {

    var userRegisterLiveData = MutableLiveData<FirebaseUser>()
    var userLoginLiveData = MutableLiveData<FirebaseUser>()
    var failureLiveData = MutableLiveData<String>()

    fun register(emailSignUp: String, passwordSignUp: String) {
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(
            emailSignUp,
            passwordSignUp
        )
            .addOnSuccessListener {
                userRegisterLiveData.value = it.user
            }
            .addOnFailureListener {
                failureLiveData.value = it.message
            }
    }

    fun login(emailSignIn: String, passwordSignIn: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(emailSignIn, passwordSignIn)
            .addOnSuccessListener {
                userLoginLiveData.value = it.user
            }
            .addOnFailureListener {
                failureLiveData.value = it.message
            }
    }
}