package com.sample.firebaseapp.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class DashboardActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>()


    private var userName: String? = null
    private var email: String? = null

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
}