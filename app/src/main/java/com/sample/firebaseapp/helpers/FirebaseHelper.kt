package com.sample.firebaseapp.helpers

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.model.UserModel
import java.util.UUID

object FirebaseHelper {

    fun getCurrentUserModel(callback: (UserModel?) -> Unit) {
        if (Firebase.auth.currentUser != null) {
            Firebase.database.reference.child("Users").child(Firebase.auth.currentUser?.uid ?: "")
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

    fun getUserPhotoUrl(userId: String?, callback: (String?) -> Unit) {
        if (userId != null) {
            Firebase.database.reference.child("Users").child(userId).child("imageUrl")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val photoUrl = snapshot.value as? String
                        callback(photoUrl)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback(null)
                    }
                })
        }

    }

    fun getUserEmail(userId: String?, callback: (String) -> Unit) {
        if (userId != null) {
            Firebase.database.reference.child("Users").child(userId).child("userMail")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userEmail = snapshot.value as? String
                        callback(userEmail ?: "")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback("")
                    }
                })
        } else {
            callback("")
        }
    }

    fun getUserName(userId: String?, callback: (String) -> Unit) {
        if (userId != null) {
            Firebase.database.reference.child("Users").child(userId).child("name")
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val userName = snapshot.value as? String
                        callback(userName ?: "")
                    }

                    override fun onCancelled(error: DatabaseError) {
                        callback("")
                    }
                })
        } else {
            callback("")
        }
    }
}