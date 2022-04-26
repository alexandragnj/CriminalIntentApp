package com.example.criminalintentapp

import androidx.lifecycle.ViewModel

class CrimeListViewModel : ViewModel() {

    val crimes = mutableListOf<Crime>()

    init {
        for (i in 0 until NUMBER_OF_ALL_CRIMES) {
            val crime = Crime()
            crime.title = "Crime #$i"
            crime.isSolved = i % 2 == 0
            crimes += crime
        }
    }

    companion object {
        private const val NUMBER_OF_ALL_CRIMES = 100
    }
}
