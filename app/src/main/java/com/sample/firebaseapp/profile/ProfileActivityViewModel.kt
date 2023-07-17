package com.sample.firebaseapp.profile

import android.app.Activity
import android.app.AlertDialog
import android.app.Application
import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sample.firebaseapp.model.UserModel
import java.io.ByteArrayOutputStream

class ProfileActivityViewModel(application: Application): AndroidViewModel(application){


    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var userId: String

    fun init(auth: FirebaseAuth, storage: FirebaseStorage, userId: String) {
        this.auth = auth
        this.storage = storage
        this.userId = userId
    }



    fun fetchUserProfile(
        userRef: DatabaseReference,
        onSuccess: (UserModel) -> Unit,
        onError: (DatabaseError) -> Unit
    ) {
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                user?.let {
                    user.imageUrl?.let { profilePictureUrl ->
                        user.imageUrl = profilePictureUrl
                    }
                    onSuccess(user)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                onError(error)
            }
        })
    }


    fun uploadProfilePicture(
        bitmap: Bitmap,
        profilePictureRef: StorageReference,
        onComplete: () -> Unit,
        onError: (Exception) -> Unit
    ) {
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = profilePictureRef.putBytes(data)
        uploadTask.addOnSuccessListener { uploadTaskSnapshot ->
            profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build()

                auth.currentUser?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userRef = FirebaseDatabase.getInstance().reference
                                .child("Users")
                                .child(userId)
                                .child("imageUrl")
                            userRef.setValue(uri.toString())
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        onComplete()
                                    } else {
                                        onError(Exception("Failed to update profile picture URL in the database"))
                                    }
                                }
                        } else {
                            onError(Exception("Failed to update profile picture in Firebase Authentication"))
                        }
                    }
            }
        }.addOnFailureListener { exception ->
            onError(exception)
        }
    }


     fun selectImage(activity: Activity) {
        val options = arrayOf<CharSequence>("Kamera", "Galeri", "İptal")
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Profil fotoğrafı seç")
        builder.setItems(options) { _, item ->
            when {
                options[item] == "Kamera" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    activity.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
                }
                options[item] == "Galeri" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    activity.startActivityForResult(intent, REQUEST_IMAGE_PICK)
                }
                options[item] == "İptal" -> {
                    builder.setCancelable(true)
                }
            }
        }
        builder.show()
    }

    companion object {
         const val REQUEST_IMAGE_CAPTURE = 1
         const val REQUEST_IMAGE_PICK = 2
    }
}

