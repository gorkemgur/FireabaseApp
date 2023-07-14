package com.sample.firebaseapp.ui.register

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.model.UserModel
import java.io.ByteArrayOutputStream
import java.util.UUID

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    private var authentication: FirebaseAuth = Firebase.auth

    private var databaseReference: DatabaseReference = Firebase.database.reference

    private var storageReference: StorageReference = FirebaseStorage.getInstance().reference

    private var email: String? = null

    private var password: String? = null

    private var name: String? = null

    private var surName: String? = null

    private var userModel: UserModel? = null

    private var userId: String? = null

    private var photoUrl: String? = null

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getEmail(): String? {
        if (email == null || email == "") {
            email = Firebase.auth.currentUser?.email
        }
        return email
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getName(): String? {
        return name
    }

    fun setSurname(surName: String?) {
        this.surName = surName
    }

    fun getPhotoUrl(): String? {
        return photoUrl
    }

    fun setPhotoUrl(photoUrl: String?) {
        this.photoUrl = photoUrl
    }

    fun register(requestListener: RequestListener) {
        if (authentication.currentUser != null)
            return

        if (email.isNullOrEmpty() || password.isNullOrEmpty() || name.isNullOrEmpty() || surName.isNullOrEmpty()) {
            requestListener.onFailed(java.lang.Exception("Boş Alan Olmamalı"))
            return
        }


        authentication.createUserWithEmailAndPassword(email ?: "", password ?: "")
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(p0: Task<AuthResult>) {
                    if (p0.isSuccessful) {
                        if (p0.result.user != null) {
                            userId = p0.result.user?.uid

                            saveUserToDatabase(requestListener)
                        }
                    } else {
                        p0.exception?.let { requestListener.onFailed(it) }
                    }
                }

            })
    }

    private fun saveUserToDatabase(requestListener: RequestListener) {
        userModel = UserModel(name, surName, userId, photoUrl)
        databaseReference.child("Users").child(userId ?: "")
            .setValue(userModel)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    authentication.signOut()
                    requestListener.onSuccess()
                } else {
                    requestListener.onFailed(Exception("Kayıt Başarısız"))
                }

            }
    }

    fun uploadProfilePhoto(
        userId: String,
        bitmap: Bitmap?,
        requestListener: RequestListener
    ) {
        bitmap?.let {imageBitmap ->
            val photoRef = storageReference.child("profile_photos/${UUID.randomUUID()}.jpg")

            val baos = ByteArrayOutputStream()
            imageBitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
            val data = baos.toByteArray()

            val uploadTask = photoRef.putBytes(data)
            uploadTask.continueWithTask { task ->
                if (!task.isSuccessful) {
                    task.exception?.let { throw it }
                }
                photoRef.downloadUrl
            }.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    photoUrl = task.result.toString()
                    saveUserToDatabase(requestListener)
                } else {
                    requestListener.onFailed(Exception("Failed to upload profile photo"))
                }
            }
        } ?: run { saveUserToDatabase(requestListener) }
    }
}