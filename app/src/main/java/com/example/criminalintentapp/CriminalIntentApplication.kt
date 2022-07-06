package com.example.criminalintentapp

import android.app.Application
import com.example.criminalintentapp.data.database.CrimeDatabase
import com.example.criminalintentapp.data.repository.CrimeRepository
import com.example.criminalintentapp.presentation.authentication.appModule
import com.example.criminalintentapp.presentation.authentication.appModuleDB
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class CriminalIntentApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Start Koin
        startKoin{
            androidLogger()
            androidContext(this@CriminalIntentApplication)
            modules(appModule, appModuleDB)
        }
    }
}
