package com.example.criminalintentapp.presentation.authentication

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module


val appModule = module {

    // single instance of HelloRepository
    single<FirebaseAuthService> { FirebaseAuthServiceImpl() }

    // MyViewModel ViewModel
    viewModel { AuthenticationViewModel(get()) }
}