package com.example.criminalintentapp.presentation.fragments.crime_detail

import android.util.Log
import androidx.lifecycle.*
import androidx.navigation.fragment.NavHostFragment
import com.example.criminalintentapp.R
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.data.database.FirestoreClass
import com.example.criminalintentapp.data.repository.CrimeRepository
import com.example.criminalintentapp.services.FirestoreService
import kotlinx.coroutines.launch

class CrimeDetailViewModel(
    private val crimeRepository: CrimeRepository,
    val firestoreService: FirestoreService
) : ViewModel() {

    private val crimeIdLiveData = MutableLiveData<Long>()
    var crime = Crime()

    val crimeLiveData: LiveData<Crime?> =
        Transformations.switchMap(crimeIdLiveData) { crimeId ->
            crimeRepository.getCrime(crimeId)
        }

    fun loadCrime(crimeId: Long) {
        crimeIdLiveData.value = crimeId
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

    fun createOrModifyCrime(): Boolean {
        if (crimeLiveData.value == null) {
            addCrime(crime)
            viewModelScope.launch {
                firestoreService.saveCrime(crime)
            }

            return true
        } else {
            saveCrime(crime)

            return false
        }
    }

    fun updateFirestore(crimeHashMap: HashMap<String, Any>) {
        viewModelScope.launch {
            firestoreService.updateCrime(crime, crimeHashMap)
        }
    }

    fun deleteFirestore(){
        viewModelScope.launch {
            firestoreService.deleteCrime(crime)
        }
    }
}
