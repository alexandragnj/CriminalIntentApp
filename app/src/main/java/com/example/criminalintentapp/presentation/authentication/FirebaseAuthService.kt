package com.example.criminalintentapp.presentation.authentication

interface FirebaseAuthService {
    fun login(email: String, password: String): Result<String,String>
}