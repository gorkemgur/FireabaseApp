package com.sample.firebaseapp.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.R
import com.sample.firebaseapp.chat.GroupChatActivity
import com.sample.firebaseapp.databinding.ActivityDashBoardBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.ui.register.RegisterActivity
import com.sample.firebaseapp.ui.register.RegisterViewModel

class DashBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashBoardBinding

    private val viewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashBoardBinding.inflate(layoutInflater)

        intent?.let {
            if (it.hasExtra("userName")) {
                viewModel.setUserName(it.extras?.getString("userName"))
            }

            if (it.hasExtra("userEmail")) {
                viewModel.setEmail(it.extras?.getString("userEmail"))
            }

            if (it.hasExtra("userImage")) {
                Glide.with(binding.root)
                    .load(it.extras?.getString("userImage"))
                    .placeholder(R.drawable.ic_default_profile_photo)
                    .into(binding.profilePhotoImageView)
            }
        }

        setUI()

        loadUserImage()

        setContentView(binding.root)
    }

    private fun loadUserImage() {
        val currentUser = Firebase.auth.currentUser
        if (currentUser != null) {
            FirebaseHelper.getUserPhotoUrl(currentUser.uid) { photoUrl ->
                if (photoUrl != null) {
                    Glide.with(this)
                        .load(photoUrl)
                        .placeholder(R.drawable.ic_default_profile_photo)
                        .into(binding.profilePhotoImageView)
                }
            }
        }
    }

    private fun setUI() {
        binding.logoutButton.setOnClickListener {
            Firebase.auth.signOut()
            startActivity(Intent(this@DashBoardActivity, RegisterActivity::class.java))
        }
        binding.chatButton.setOnClickListener {
            startActivity(Intent(this@DashBoardActivity, GroupChatActivity::class.java))
        }
        setLoggedInUI()
    }


    private fun setLoggedInUI() {
        binding.logoutButton.visibility = View.VISIBLE
        binding.userNameTextViewValue.text = viewModel.getUserName()
        binding.userEmailTextViewValue.text = viewModel.getEmail()
        binding.logoutButton.isEnabled = true
    }


}