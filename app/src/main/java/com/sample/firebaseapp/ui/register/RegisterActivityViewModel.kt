package com.sample.firebaseapp.ui.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.model.UserModel

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    private var authentication: FirebaseAuth = Firebase.auth

    private var databaseReference: DatabaseReference = Firebase.database.reference

    private var email: String? = null

    private var password: String? = null

    private var name: String? = null

    private var surName: String? = null

    private var userModel: UserModel? = null

    private var userId: String? = null

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
        userModel = UserModel(name, surName, userId, "")
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
}