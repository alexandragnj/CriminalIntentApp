package com.example.criminalintentapp

import android.app.Application
import com.example.criminalintentapp.database.CrimeDatabase

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        val crimeDao = CrimeDatabase.getDatabase(this).crimeDao()
        CrimeRepository.initialize(crimeDao)
    }
}
