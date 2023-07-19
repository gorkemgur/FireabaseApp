package com.sample.firebaseapp.ui.common.profile

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.model.UserModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>()

    private var userModel: UserModel? = null

    private var isCurrentUser: Boolean? = null

    private val authentication: FirebaseAuth = Firebase.auth

    private val storageRef = Firebase.storage.reference

    private var databaseRef: DatabaseReference = Firebase.database.reference

    private var imageUri: Uri? = null

    private var userImageURL: String? = null

    fun setUserModel(userModel: UserModel?) {
        this.userModel = userModel
        if (authentication.currentUser?.uid == userModel?.userId) {
            isCurrentUser = true
        }
    }

    fun isCurrentUser(): Boolean? {
        return isCurrentUser
    }

    fun getUserName(): String {
        return userModel?.name.plus(" ${userModel?.surName}")
    }

    fun getUserEmail(): String? {
        return userModel?.email
    }

    fun getUserImageUrl(): String {
        return userModel?.imageUrl ?: ""
    }

    fun uploadUserProfileImage(
        requestListener: RequestListener
    ) {

        userModel?.userId?.let { userId ->
            imageUri?.let {
                storageRef.child("images/").child(userId).putFile(it).addOnSuccessListener {
                    if (it.task.isSuccessful) {
                        storageRef.child("images/").child(userId).downloadUrl.addOnSuccessListener { downloadUrl ->
                            Log.e("download", downloadUrl.toString())
                            userImageURL = downloadUrl.toString()
                            saveUserProfileToDatabase(object : RequestListener {
                                override fun onSuccess() {
                                    requestListener.onSuccess()
                                }

                                override fun onFailed(e: Exception) {
                                    requestListener.onFailed(e)
                                }
                            })
                        }
                    }
                }
            }
        }
    }

    private fun saveUserProfileToDatabase(requestListener: RequestListener) {
        databaseRef.child("Users/${userModel?.userId}")
            .child("imageUrl")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.ref.setValue(userImageURL)
                    requestListener.onSuccess()
                }

                override fun onCancelled(error: DatabaseError) {
                    requestListener.onFailed(error.toException())
                }

            })
    }

    fun setImageUri(imageUri: Uri) {
        this.imageUri = imageUri
    }

    fun isImageUriEmpty(): Boolean {
        return imageUri == null
    }

}