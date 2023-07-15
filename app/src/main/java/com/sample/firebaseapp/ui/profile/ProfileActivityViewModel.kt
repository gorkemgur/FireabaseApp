package com.sample.firebaseapp.ui.profile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.sample.firebaseapp.helpers.FirebaseHelper

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private var userProfile: String? = null
    private var userName: String? = null
    private var email: String? = null

    fun setUserProfile(userId: String?, callback: (String?) -> Unit) {
        FirebaseHelper.getUserPhotoUrl(userId) { photoUrl ->
            this.userProfile = photoUrl
            callback(photoUrl)
        }
    }

    fun getUserProfile(callback: (String?) -> Unit) {
        callback(userProfile)
    }

    fun setUserName(userId: String?, callback: (String?) -> Unit) {
        FirebaseHelper.getUserName(userId) { userName ->
            this.userName = userName
            callback(userName)
        }
    }

    fun getUserName(callback: (String?) -> Unit) {
        callback(userName)
    }

    fun setEmail(userId: String?, callback: (String?) -> Unit) {
        FirebaseHelper.getUserEmail(userId) { userEmail ->
            this.email = userEmail
            callback(userEmail)
        }
    }

    fun getEmail(callback: (String?) -> Unit) {
        callback(email)
    }
}