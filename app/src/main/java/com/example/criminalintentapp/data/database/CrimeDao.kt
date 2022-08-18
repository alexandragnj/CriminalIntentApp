package com.example.criminalintentapp.data.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CrimeDao {

    @Query("SELECT * FROM crime")
    fun getCrimes(): LiveData<List<Crime>>

    @Query("SELECT * FROM crime WHERE id=(:id)")
    fun getCrime(id: Long): LiveData<Crime?>

    @Update
    fun updateCrime(crime: Crime)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun addCrime(crime: Crime)

    @Query("DELETE FROM crime WHERE id = (:id)")
    fun deleteCrime(id: Long)
}
