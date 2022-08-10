package com.example.criminalintentapp.presentation.fragments.crime_list

import android.util.Log
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.data.database.FirestoreClass
import com.example.criminalintentapp.data.repository.CrimeRepository

class CrimeListViewModel(crimeRepository: CrimeRepository) : ViewModel() {

    val crimesListLiveData = crimeRepository.getCrimes()

    fun syncWithCloud(){

        crimesListLiveData.observeForever(){
            FirestoreClass().getCrimes()
            Log.d("CrimeListViewModel", "Firestore sync")
        }
    }
}
