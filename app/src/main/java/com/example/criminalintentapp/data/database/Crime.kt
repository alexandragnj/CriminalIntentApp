package com.example.criminalintentapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Crime(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,

    var title: String = "",
    var date: String = "",
    var time: String = "",
    var isSolved: Boolean = false,
    var suspect: String = "",
    val photoFileName: String = "IMG_${System.currentTimeMillis()}.jpg"
)
