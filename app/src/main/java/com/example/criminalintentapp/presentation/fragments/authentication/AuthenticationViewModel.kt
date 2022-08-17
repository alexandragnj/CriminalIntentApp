package com.example.criminalintentapp.presentation.fragments.authentication

import android.content.Intent
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintentapp.models.User
import com.example.criminalintentapp.services.FirebaseAuthService
import com.example.criminalintentapp.services.FirestoreService
import com.example.criminalintentapp.utils.onFailure
import com.example.criminalintentapp.utils.onSuccess
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import kotlinx.coroutines.launch

class AuthenticationViewModel(val authService: FirebaseAuthService, val firestoreService: FirestoreService) : ViewModel() {

    var userRegisterLiveData = MutableLiveData<FirebaseUser>()
    var userLoginLiveData = MutableLiveData<FirebaseUser>()
    var failureLiveData = MutableLiveData<String>()
    val currentUser = FirebaseAuth.getInstance().currentUser
    var callbackManager: CallbackManager = CallbackManager.Factory.create()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            authService.login(email, password).onSuccess { user ->
                userLoginLiveData.value = user
            }.onFailure { exception ->
                if (exception is FirebaseAuthInvalidCredentialsException)
                    failureLiveData.value =
                        "The password is invalid or the user does not have a password."
            }
        }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            authService.register(email, password).onSuccess { user ->
                userRegisterLiveData.value = user
                viewModelScope.launch{
                    firestoreService.saveUser(User(user.uid, email))
                }
            }.onFailure { exception ->
                if (exception is FirebaseAuthUserCollisionException)
                    failureLiveData.value = "This email is already in use. Please login."
            }
        }
    }

    fun facebookLogin(): FacebookCallback<LoginResult> {
       return object :
            FacebookCallback<LoginResult> {
            override fun onSuccess(result: LoginResult) {
                Log.d(LoginFragment.TAG, "facebook:onSuccess:$result")
                val credential =
                    FacebookAuthProvider.getCredential((result.accessToken).token)
                handleLoginAccessToken(credential)
            }

            override fun onCancel() {
                Log.d(LoginFragment.TAG, "facebook:onCancel")
            }

            override fun onError(error: FacebookException) {
                Log.d(LoginFragment.TAG, "facebook:onError", error)
            }
        }
    }

    fun googleLogin(data: Intent?) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val exception = task.exception
        if (task.isSuccessful) {
            try {
                val account = task.getResult(ApiException::class.java)!!
                val credential = GoogleAuthProvider.getCredential(account.idToken.toString(), null)
                handleLoginAccessToken(credential)
            } catch (e: ApiException) {
                Log.w(LoginFragment.TAG, "Google sign in failed", e)
            }
        } else {
            Log.w(LoginFragment.TAG, exception.toString())
        }
    }

    fun handleLoginAccessToken(credential: AuthCredential) {
        viewModelScope.launch {
            authService.singInWithFacebookOrGoogle(credential).onSuccess {
                userLoginLiveData.value = it

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