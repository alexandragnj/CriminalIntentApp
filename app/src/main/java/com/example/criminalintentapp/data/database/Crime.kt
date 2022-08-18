package com.example.criminalintentapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.common.util.Clock

@Entity
data class Crime(
    @PrimaryKey
    val id: Long = System.currentTimeMillis(),
    var title: String = "",
    var date: String = "",
    var time: String = "",
    var isSolved: Boolean = false,
    var suspect: String = "",
    val photoFileName: String = "IMG_${System.currentTimeMillis()}.jpg"
)
