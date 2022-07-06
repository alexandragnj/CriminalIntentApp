package com.example.criminalintentapp.presentation.authentication

import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthService {
    suspend fun login(email: String, password: String): Result<Exception, FirebaseUser>

    suspend fun register(email: String, password: String): Result<Exception, FirebaseUser>
}