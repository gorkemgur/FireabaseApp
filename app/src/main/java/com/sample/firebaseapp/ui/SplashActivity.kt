package com.sample.firebaseapp.ui

import android.content.Intent
import android.os.Bundle
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.dashboard.DashBoardActivity
import com.sample.firebaseapp.databinding.ActivitySplashBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.ui.common.BaseActivity
import com.sample.firebaseapp.ui.register.RegisterActivity

class SplashActivity : BaseActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)

        checkCurrentUser()

    }

    private fun checkCurrentUser() {
        showLoadingProgressBar("Lütfen Bekleyin")
        FirebaseHelper.getCurrentUserModel { userModel ->
            dismissProgressBar()
            if (userModel != null) {
                finish()
                val intent = Intent(this@SplashActivity, DashBoardActivity::class.java)
                intent.putExtra("userName", userModel.name.toString())
                intent.putExtra("userEmail", Firebase.auth.currentUser?.email)
                startActivity(intent)
            } else {
                finish()
                startActivity(Intent(this@SplashActivity, RegisterActivity::class.java))
            }
        }
    }
}