package com.example.criminalintentapp.presentation.fragments.crime_list

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.criminalintentapp.data.database.FirestoreClass
import com.example.criminalintentapp.data.repository.CrimeRepository

class CrimeListViewModel(crimeRepository: CrimeRepository) : ViewModel() {

    val crimesListLiveData = crimeRepository.getCrimes()

    val crimeRep = crimeRepository

    fun syncWithCloud() {

        crimesListLiveData.observeForever() {
            FirestoreClass().getCrimes(crimeRep)
            Log.d("CrimeListViewModel", "Firestore sync")
        }
    }
}
