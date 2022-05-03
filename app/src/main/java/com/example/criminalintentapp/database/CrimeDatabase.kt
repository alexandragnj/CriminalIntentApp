package com.example.criminalintentapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.RoomDatabase
import androidx.room.Room
import com.example.criminalintentapp.Crime
import com.example.criminalintentapp.CrimeRepository

@Database(entities = [Crime::class], version = 1, exportSchema = false)
@TypeConverters(CrimeTypeConverters::class)
abstract class CrimeDatabase : RoomDatabase() {

    abstract fun crimeDao(): CrimeDao

    companion object {
        fun getDatabase(context: Context): CrimeDatabase {
            val database = Room.databaseBuilder(
                context.applicationContext,
                CrimeDatabase::class.java,
                CrimeRepository.DATABASE_NAME
            ).build()

            return database
        }
    }
}