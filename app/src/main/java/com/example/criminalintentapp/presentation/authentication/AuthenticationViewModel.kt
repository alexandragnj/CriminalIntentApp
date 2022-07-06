package com.example.criminalintentapp.presentation.authentication

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthenticationViewModel(val service: FirebaseAuthService) : ViewModel() {

    var userRegisterLiveData = MutableLiveData<FirebaseUser>()
    var userLoginLiveData = MutableLiveData<FirebaseUser>()
    var failureLiveData = MutableLiveData<String>()
    val currentUser = FirebaseAuth.getInstance().currentUser

    fun login(email: String, password: String) {
        viewModelScope.launch {
            service.login(email, password).onSuccess { user ->
                userLoginLiveData.value = user
            }.onFailure { exception ->
                failureLiveData.value = exception.toString()
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            service.register(email, password).onSuccess { user ->
                userRegisterLiveData.value = user
            }.onFailure { exception ->
                failureLiveData.value = exception.toString()
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