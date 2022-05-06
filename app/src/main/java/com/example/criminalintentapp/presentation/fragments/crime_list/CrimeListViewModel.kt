package com.example.criminalintentapp.presentation.fragments.crime_list

import androidx.lifecycle.ViewModel
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.data.repository.CrimeRepository

class CrimeListViewModel : ViewModel() {

    private val crimeRepository = CrimeRepository.get()
    val crimesListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }
}
