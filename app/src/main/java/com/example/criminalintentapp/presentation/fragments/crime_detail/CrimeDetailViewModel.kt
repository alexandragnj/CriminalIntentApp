package com.example.criminalintentapp.presentation.fragments.crime_detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.data.repository.CrimeRepository

class CrimeDetailViewModel(private val crimeRepository: CrimeRepository) : ViewModel() {

    private val crimeIdLiveData = MutableLiveData<Long>()
    var crime = Crime()

    val crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: Long) {
        crimeIdLiveData.value= crimeId
    }

    fun deleteCrime(crimeId: Long) {
        crimeIdLiveData.value = crimeId
        crimeRepository.deleteCrime(crimeId)
    }

    fun saveCrime(crime: Crime) {
        crimeRepository.updateCrime(crime)
    }

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }

    /*fun deleteCrime(crime: Crime){
        crimeRepository.deleteCrime(crime)
    }*/
}
