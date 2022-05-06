package com.example.criminalintentapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity
data class Crime(
    @PrimaryKey(autoGenerate = true) val id: Int? = 0,

    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false
)
