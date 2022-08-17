package com.example.criminalintentapp.services

import android.util.Log
import com.example.criminalintentapp.data.database.Crime
import com.example.criminalintentapp.data.repository.CrimeRepository
import com.example.criminalintentapp.models.User
import com.example.criminalintentapp.utils.Paths
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreServiceImpl: FirestoreService {

    private val firestore = FirebaseFirestore.getInstance()
    private val users = Paths.users.name
    private val crimes = Paths.crimes.name

    override suspend fun saveUser(user: User) {
        firestore.collection(users)
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnCompleteListener {
                Log.d(TAG, "User saved success")
            }
            .addOnFailureListener {
                Log.d(TAG, "User saved failed")
            }
    }

    override suspend fun saveCrime(crime: Crime) {
        firestore.collection(crimes)
            .document(crime.id.toString())
            .set(crime, SetOptions.merge())
            .addOnCompleteListener {
                Log.d(TAG, "Crime saved success")
            }
            .addOnFailureListener {
                Log.d(TAG, "Crime saved failed")
            }
    }

    override suspend fun deleteCrime(crime: Crime) {
        firestore.collection(crimes)
            .document(crime.id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "Detelted success")
            }
            .addOnFailureListener {
                Log.d(TAG, "Detelted failed")
            }
    }

    override suspend fun getCrimes(crimeRepository: CrimeRepository) {
        firestore.collection(crimes).get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    for (data in snapshot.documents) {
                        val crime: Crime? = data.toObject(Crime::class.java)
                        if (crime != null) {
                            Log.d(TAG, "read data: $crime")
                            addCrime(crime, crimeRepository)
                        }
                    }

                    Log.d(TAG, "Show crimes success")
                }
            }
            .addOnFailureListener {
                Log.d(TAG, "Show crimes failed")
            }
    }

    override suspend fun updateCrime(crime: Crime, crimeHashMap: HashMap<String, Any>) {
        firestore.collection(crimes).document(crime.id.toString()).update(crimeHashMap)
            .addOnSuccessListener {
                Log.d(TAG, "Update crimes success")
            }
            .addOnFailureListener {
                Log.d(TAG, "Update crimes failed")
            }
    }

    private fun addCrime(crime: Crime, crimeRepository: CrimeRepository) {
        crimeRepository.addCrime(crime)
    }

    companion object{
        private const val TAG="FirestoreService"
    }
}