package com.example.criminalintentapp.services

import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.data.repository.CrimeRepository
import com.example.criminalintentapp.models.User

interface FirestoreService {
    suspend fun saveUser(user: User)

    suspend fun saveCrime(crime: Crime)

    suspend fun deleteCrime(crime: Crime)

    suspend fun getCrimes(crimeRepository: CrimeRepository)

    suspend fun updateCrime(crime: Crime, crimeHashMap: HashMap<String, Any>)
}