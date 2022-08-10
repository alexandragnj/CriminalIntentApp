package com.example.criminalintentapp.presentation.fragments.crime_list

import androidx.lifecycle.ViewModel
import com.example.criminalintentapp.data.database.FirestoreClass
import com.example.criminalintentapp.data.repository.CrimeRepository

class CrimeListViewModel(crimeRepository: CrimeRepository) : ViewModel() {

    val crimesListLiveData = crimeRepository.getCrimes()

    private val crimeRepo = crimeRepository

    fun syncWithCloud() {

        crimesListLiveData.observeForever() {
            FirestoreClass().getCrimes(crimeRepo)
        }
    }
}
