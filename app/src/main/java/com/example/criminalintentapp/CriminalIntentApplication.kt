package com.example.criminalintentapp

import android.app.Application
import com.example.criminalintentapp.data.database.CrimeDatabase
import com.example.criminalintentapp.data.repository.CrimeRepository

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val crimeDao = CrimeDatabase.getDatabase(this).crimeDao()
        val filesDir = this.applicationContext.filesDir
        CrimeRepository.initialize(crimeDao, filesDir)
    }
}
