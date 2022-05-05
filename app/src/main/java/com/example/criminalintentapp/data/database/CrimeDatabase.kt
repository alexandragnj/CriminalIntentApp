package com.example.criminalintentapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.criminalintentapp.data.repository.CrimeRepository

@Database(entities = [Crime::class], version = 1)
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
