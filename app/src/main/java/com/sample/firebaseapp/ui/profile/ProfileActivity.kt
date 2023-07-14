package com.sample.firebaseapp.ui.profile

import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.sample.firebaseapp.R
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import com.sample.firebaseapp.ui.common.BaseActivity

class ProfileActivity: BaseActivity() {
    private lateinit var binding: ActivityProfileBinding

    private val viewModel : ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)


        intent?.let {
            if (it.hasExtra("userName")) {
                val userName = it.getStringExtra("userName")
                binding.nameTextView.text = userName
                viewModel.setUserName(userName)
            }

            if (it.hasExtra("userImage")) {
                val userImage = it.getStringExtra("userImage")
                Glide.with(binding.root)
                    .load(userImage)
                    .placeholder(R.drawable.ic_default_profile_photo)
                    .into(binding.profilePhotoImageView)
                viewModel.setUserProfile(userImage)
            }
        }

        binding.emailTextView.text = "default"

        setContentView(binding.root)
    }
}