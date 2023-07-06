package com.sample.firebaseapp.ui.login

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.helpers.FirebaseHelper

class LoginActivityViewModel(application: Application) : AndroidViewModel(application) {

    private val context = getApplication<Application>()

    private var email: String? = null

    private var password: String? = null

    private var userName: String? = null

    private var authentication: FirebaseAuth = Firebase.auth


    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun getUserName(): String? {
        return userName
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getEmail(): String? {
        return email
    }

    fun setPassword(password: String?) {
        this.password = password
    }

    fun login(requestListener: RequestListener) {

        if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
            requestListener.onFailed(java.lang.Exception("Boş Alan Olmamalı"))
            return
        }

        authentication.signInWithEmailAndPassword(email ?: "", password ?: "")
            .addOnCompleteListener(object : OnCompleteListener<AuthResult> {
                override fun onComplete(task: Task<AuthResult>) {
                    if (task.isSuccessful) {
                        FirebaseHelper.getCurrentUserModel { userModel ->
                            userName = userModel?.name
                            email = authentication.currentUser?.email
                            requestListener.onSuccess()
                        }
                    } else {
                        task.exception?.let { exception ->
                            requestListener.onFailed(exception)
                        }
                    }
                }
            })
    }


}