package com.example.criminalintentapp.data.repository

import androidx.lifecycle.LiveData
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.data.database.CrimeDao
import java.io.File
import java.util.concurrent.Executors

class CrimeRepository private constructor(
    private val crimeDao: CrimeDao,
    private val filesDir: File?
) {

    private val executor = Executors.newSingleThreadExecutor()

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: Int): LiveData<Crime?> = crimeDao.getCrime(id)

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

    fun getPhotoFile(crime: Crime): File = File(filesDir, crime.photoFileName)

    companion object {
        const val DATABASE_NAME = "crime-database"

        private var INSTANCE: CrimeRepository? = null

        fun initialize(crimeDao: CrimeDao, filesDir: File?) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(crimeDao, filesDir)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialize")
        }
    }
}
