package com.example.criminalintentapp.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.criminalintentapp.R
import com.example.criminalintentapp.presentation.fragments.crime_detail.CrimeFragment
import com.example.criminalintentapp.presentation.fragments.crime_list.CrimeListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
