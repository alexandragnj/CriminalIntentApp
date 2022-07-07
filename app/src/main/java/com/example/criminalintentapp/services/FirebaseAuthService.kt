package com.example.criminalintentapp.services

import com.google.firebase.auth.FirebaseUser
import com.example.criminalintentapp.utils.Result

interface FirebaseAuthService {
    suspend fun login(email: String, password: String): Result<Exception, FirebaseUser>

    suspend fun register(email: String, password: String): Result<Exception, FirebaseUser>
}