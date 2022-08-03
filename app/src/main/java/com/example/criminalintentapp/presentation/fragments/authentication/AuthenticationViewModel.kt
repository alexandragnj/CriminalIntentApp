package com.example.criminalintentapp.presentation.fragments.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintentapp.services.FirebaseAuthService
import com.example.criminalintentapp.utils.onFailure
import com.example.criminalintentapp.utils.onSuccess
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthenticationViewModel(val authService: FirebaseAuthService) : ViewModel() {

    var userRegisterLiveData = MutableLiveData<FirebaseUser>()
    var userLoginLiveData = MutableLiveData<FirebaseUser>()
    var failureLiveData = MutableLiveData<String>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authService.login(email, password).onSuccess { user ->
                userLoginLiveData.value = user
            }.onFailure { exception ->
                if(exception is FirebaseAuthInvalidCredentialsException)
                failureLiveData.value = "The password is invalid or the user does not have a password."
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            authService.register(email, password).onSuccess { user ->
                userRegisterLiveData.value = user
            }.onFailure { exception ->
                if (exception is FirebaseAuthUserCollisionException)
                    failureLiveData.value = "This email is already in use. Please login."
            }
        }
    }

    fun checkFields(email: String, password: String): Boolean {
        if (email == "" || password == "") {
            return false
        }
        return true
    }
}