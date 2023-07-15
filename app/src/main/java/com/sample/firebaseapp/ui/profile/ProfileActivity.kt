package com.sample.firebaseapp.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.sample.firebaseapp.R
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.ui.common.BaseActivity

class ProfileActivity: BaseActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val viewModel : ProfileViewModel by viewModels()

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)


        intent?.let {
            if (it.hasExtra("userId")) {
                userId = it.getStringExtra("userId")

                viewModel.setUserProfile(userId) { userProfile ->
                    if (userProfile != null) {
                        Glide.with(this)
                            .load(userProfile)
                            .placeholder(R.drawable.ic_default_profile_photo)
                            .into(binding.profilePhotoImageView)
                    }
                }

                viewModel.setEmail(userId.toString()) { userEmail ->
                    binding.emailTextView.text = userEmail
                }

                viewModel.setUserName(userId.toString()) { userName ->
                    binding.nameTextView.text = userName
                }


            }
        }

        setContentView(binding.root)
    }
}