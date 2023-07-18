package com.sample.firebaseapp.helpers

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.model.UserModel

object FirebaseHelper {

    private val reference = Firebase.database.reference

    fun getCurrentUserModel(callback: (UserModel?) -> Unit) {
        if (Firebase.auth.currentUser != null) {
            reference.child("Users").child(Firebase.auth.currentUser?.uid ?: "")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        callback(snapshot.getValue(UserModel::class.java))
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null)
                    }
                })
        } else {
            callback(null)
        }
    }


    fun getUserModelWithUserId(userId: String?, callback: (UserModel?) -> Unit) {
        reference.child("Users").child(userId ?: "").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.getValue(UserModel::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                callback(null)
            }
        })
    }
}