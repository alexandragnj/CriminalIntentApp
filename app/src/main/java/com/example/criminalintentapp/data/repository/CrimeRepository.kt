package com.example.criminalintentapp.data.repository

import androidx.lifecycle.LiveData
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.data.database.CrimeDao
import java.util.concurrent.Executors

class CrimeRepository(private val crimeDao: CrimeDao) {

    private val executor = Executors.newSingleThreadExecutor()

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: Long): LiveData<Crime?> = crimeDao.getCrime(id)

    fun deleteCrime(id: Long) {
        executor.execute {
            crimeDao.deleteCrime(id)
        }
    }

    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    companion object {
        const val DATABASE_NAME = "crime-database"
    }
}
