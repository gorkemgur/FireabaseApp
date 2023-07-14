package com.sample.firebaseapp.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private var userProfile: String? = null
    private var userName: String? = null
    private var email: String? = null

    fun setUserProfile(userProfile: String?) {
        this.userProfile = userProfile
    }

    fun getUserProfile(): String? {
        return userProfile
    }

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