package com.example.criminalintentapp.utils

import androidx.room.Room
import com.example.criminalintentapp.data.database.CrimeDatabase
import com.example.criminalintentapp.data.database.migration_1_2
import com.example.criminalintentapp.data.repository.CrimeRepository
import com.example.criminalintentapp.presentation.fragments.authentication.AuthenticationViewModel
import com.example.criminalintentapp.presentation.fragments.crime_detail.CrimeDetailViewModel
import com.example.criminalintentapp.presentation.fragments.crime_list.CrimeListViewModel
import com.example.criminalintentapp.services.FirebaseAuthService
import com.example.criminalintentapp.services.FirebaseAuthServiceImpl
import com.example.criminalintentapp.services.FirestoreService
import com.example.criminalintentapp.services.FirestoreServiceImpl
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<FirebaseAuthService> { FirebaseAuthServiceImpl() }
    single<FirestoreService> { FirestoreServiceImpl() }

    viewModel { AuthenticationViewModel(authService = get(), firestoreService = get()) }
    viewModel { CrimeDetailViewModel(crimeRepository = get(), firestoreService = get()) }
    viewModel { CrimeListViewModel(crimeRepository = get(), firestoreService = get()) }
}

val appModuleDB = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            CrimeDatabase::class.java,
            CrimeRepository.DATABASE_NAME
        ).addMigrations(migration_1_2).build()
    }

    single {
        val database = get<CrimeDatabase>()
        database.crimeDao()
    }

    single { CrimeRepository(get()) }
}