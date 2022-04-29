package com.example.criminalintentapp

import androidx.lifecycle.LiveData
import com.example.criminalintentapp.database.CrimeDao
import java.util.UUID

class CrimeRepository private constructor(private val crimeDao: CrimeDao) {

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    companion object {
        const val DATABASE_NAME = "crime-database"

        private var INSTANCE: CrimeRepository? = null

        fun initialize(crimeDao: CrimeDao) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(crimeDao)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw  IllegalStateException("CrimeRepository must be initialize")
        }
    }
}