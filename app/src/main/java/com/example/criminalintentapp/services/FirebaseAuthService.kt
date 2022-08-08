package com.example.criminalintentapp.services

import com.google.firebase.auth.FirebaseUser
import com.example.criminalintentapp.utils.Result
import com.google.firebase.auth.AuthCredential

interface FirebaseAuthService {
    suspend fun login(email: String, password: String): Result<Exception, FirebaseUser>

    suspend fun register(email: String, password: String): Result<Exception, FirebaseUser>

    suspend fun singInWithFacebookOrGoogle(credential: AuthCredential): Result<Exception, FirebaseUser>
}