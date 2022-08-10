package com.example.criminalintentapp.data.database

import android.util.Log
import com.example.criminalintentapp.models.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions

class FirestoreClass {

    private val firestore = FirebaseFirestore.getInstance()

    fun saveUser(user: User) {
        firestore.collection("users")
            .document(user.id)
            .set(user, SetOptions.merge())
            .addOnCompleteListener {
                Log.d("Firestore", "User saved success")
            }
            .addOnFailureListener {
                Log.d("Firestore", "User saved failed")
            }
    }

    fun saveCrime(crime: Crime) {
        firestore.collection("crimes")
            .document(crime.id.toString())
            .set(crime, SetOptions.merge())
            .addOnCompleteListener {
                Log.d("Firestore", "Crime saved success")
            }
            .addOnFailureListener {
                Log.d("Firestore", "Crime saved failed")
            }
    }

    fun deleteCrime(crime: Crime) {
        firestore.collection("crimes")
            .document(crime.id.toString())
            .delete()
            .addOnSuccessListener {
                Log.d("Firestore", "Detelted success")
            }
            .addOnFailureListener {
                Log.d("Firestore", "Detelted failed")
            }
    }

    fun getCrimes() {
        firestore.collection("crimes").get()
            .addOnSuccessListener { snapshot ->
                if (!snapshot.isEmpty) {
                    for (data in snapshot.documents) {
                        val crime: Crime? = data.toObject(Crime::class.java)
                        if (crime != null) {
                            Log.d(TAG,"read data: $crime")
                        }
                    }

                    Log.d("Firestore", "Show crimes success")
                }
            }
            .addOnFailureListener {
                Log.d("Firestore", "Show crimes failed")
            }

    }

    companion object {
        private const val TAG = "FirestoreClass"
    }
}