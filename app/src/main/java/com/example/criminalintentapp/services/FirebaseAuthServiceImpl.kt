package com.example.criminalintentapp.services

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import com.example.criminalintentapp.utils.Result

class FirebaseAuthServiceImpl : FirebaseAuthService {

    override suspend fun login(email: String, password: String): Result<Exception, FirebaseUser> {
        val task = FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
        val result = task.await()
        return handleResult(task, result)
    }

    override suspend fun register(
        email: String,
        password: String
    ): Result<Exception, FirebaseUser> {
        val task = FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
        val result = task.await()
        return handleResult(task, result)
    }

    private fun handleResult(
        task: Task<AuthResult>,
        result: AuthResult
    ): Result<Exception, FirebaseUser> {
        return if (task.isSuccessful) {
            handleTaskSuccessful(result)
        } else {
            handleTaskFailed(task)
        }
    }

    private fun handleTaskSuccessful(result: AuthResult): Result<Exception, FirebaseUser> {
        return result.user?.let { Result.Success(it) } ?: Result.Failure(Exception("User is null"))
    }

    private fun handleTaskFailed(task: Task<AuthResult>): Result<Exception, FirebaseUser> {
        return task.exception?.let { Result.Failure(it) }
            ?: Result.Failure(Exception("Failed with no exception"))
    }
}