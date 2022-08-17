package com.example.criminalintentapp.presentation.fragments.crime_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintentapp.data.database.FirestoreClass
import com.example.criminalintentapp.data.repository.CrimeRepository
import com.example.criminalintentapp.services.FirestoreService
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlinx.coroutines.launch

class CrimeListViewModel(val crimeRepository: CrimeRepository, val firestoreService: FirestoreService) :
    ViewModel() {

    val crimesListLiveData = crimeRepository.getCrimes()

    //private val crimeRepo = crimeRepository

    fun syncWithCloud() {

        crimesListLiveData.observeForever() {
            viewModelScope.launch {
                firestoreService.getCrimes(crimeRepository)
            }
        }
    }
}
