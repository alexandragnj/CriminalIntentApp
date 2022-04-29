package com.example.criminalintentapp

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimesListLiveData = crimeRepository.getCrimes()
}
