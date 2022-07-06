package com.example.criminalintentapp.presentation.authentication

import androidx.room.Room
import com.example.criminalintentapp.data.database.CrimeDatabase
import com.example.criminalintentapp.data.repository.CrimeRepository
import com.example.criminalintentapp.presentation.fragments.crime_detail.CrimeDetailViewModel
import com.example.criminalintentapp.presentation.fragments.crime_list.CrimeListViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    // single instance of HelloRepository
    single<FirebaseAuthService> { FirebaseAuthServiceImpl() }

    // MyViewModel ViewModel
    viewModel { AuthenticationViewModel(get()) }
}

val appModuleDB = module {

    single {
        Room.databaseBuilder(
            androidApplication(),
            CrimeDatabase::class.java,
            CrimeRepository.DATABASE_NAME
        ).build()
    }

    single {
        val database = get<CrimeDatabase>()
        database.crimeDao()
    }

    single { CrimeRepository(get()) }

    viewModel { CrimeDetailViewModel(get()) }

    viewModel { CrimeListViewModel(get()) }

}