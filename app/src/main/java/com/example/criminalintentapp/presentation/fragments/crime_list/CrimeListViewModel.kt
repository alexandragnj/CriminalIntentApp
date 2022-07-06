package com.example.criminalintentapp.presentation.fragments.crime_list

import androidx.lifecycle.ViewModel
import com.example.criminalintentapp.data.repository.CrimeRepository

class CrimeListViewModel(private val crimeRepository: CrimeRepository) : ViewModel() {

    val crimesListLiveData = crimeRepository.getCrimes()
}
