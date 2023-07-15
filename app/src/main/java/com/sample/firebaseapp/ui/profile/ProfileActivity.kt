package com.sample.firebaseapp.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.sample.firebaseapp.R
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.ui.common.BaseActivity

class ProfileActivity: BaseActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val viewModel : ProfileViewModel by viewModels()

    var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)


        intent?.let {
            if (it.hasExtra("userId")) {
                userId = it.getStringExtra("userId")
                FirebaseHelper.getUserPhotoUrl(userId.toString()) { photoUrl ->
                    if (photoUrl != null) {
                        Glide.with(this)
                            .load(photoUrl)
                            .placeholder(R.drawable.ic_default_profile_photo)
                            .into(binding.profilePhotoImageView)
                        viewModel.setUserProfile(photoUrl)
                    }
                }

                FirebaseHelper.getUserEmail(userId.toString()) { userEmail ->
                    binding.emailTextView.text = userEmail
                    viewModel.setEmail(userEmail)
                }

                FirebaseHelper.getUserName(userId.toString()) { userName ->
                    binding.nameTextView.text = userName
                    viewModel.setUserName(userName)
                }

            }
        }

        setContentView(binding.root)
    }
}