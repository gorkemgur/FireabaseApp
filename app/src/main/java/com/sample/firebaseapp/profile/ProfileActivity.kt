package com.sample.firebaseapp.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.sample.firebaseapp.R
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import java.io.IOException

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var viewModel: ProfileActivityViewModel
    private lateinit var userId: String
    private var selectedBitmap: Bitmap? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                ProfileActivityViewModel.REQUEST_IMAGE_CAPTURE -> {
                    val imageBitmap = data?.extras?.get("data") as? Bitmap
                    imageBitmap?.let { bitmap ->
                        selectedBitmap = bitmap
                        binding.profileImageView.setImageBitmap(selectedBitmap)
                    }
                }
                ProfileActivityViewModel.REQUEST_IMAGE_PICK -> {
                    val uri = data?.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                        selectedBitmap = bitmap
                        binding.profileImageView.setImageBitmap(selectedBitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)


        userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""

        binding.addPhotoButton.isVisible = userId == FirebaseAuth.getInstance().currentUser?.uid
        binding.savePhotoButton.isVisible = userId == FirebaseAuth.getInstance().currentUser?.uid

        viewModel = ViewModelProvider(this).get(ProfileActivityViewModel::class.java)
        viewModel.init(userId)


        viewModel.fetchUserProfile(
            onSuccess = { user ->
                binding.emailTextView.text = user.email
                binding.nameTextView.text = user.name!!.uppercase()
                binding.surnameTextView.text = user.surName!!.uppercase()

                user.imageUrl?.let { profilePictureUrl ->
                    Glide.with(this)
                        .load(profilePictureUrl)
                        .placeholder(R.drawable.baseline_account_box_24)
                        .error(R.drawable.baseline_account_box_24)
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .into(binding.profileImageView)
                }
            },
            onError = { error ->
                Log.e(TAG, "Failed to fetch user profile: $error")
            }
        )
        binding.addPhotoButton.setOnClickListener {
            viewModel.selectImage(this)
        }

        binding.savePhotoButton.setOnClickListener {
            selectedBitmap?.let { bitmap ->
                val storageRef = FirebaseStorage.getInstance().reference
                val profilePictureRef = storageRef.child("profile_pictures/$userId")
                viewModel.uploadProfilePicture(
                    bitmap,
                    profilePictureRef,
                    onComplete = {
                        Toast.makeText(this, "Profil fotoğrafı değiştirildi", Toast.LENGTH_SHORT).show()
                    },
                    onError = { exception ->
                        Log.e(TAG, "Failed to upload profile picture: $exception")
                    }
                )
            }
        }


    }

    companion object {
        private const val TAG = "ProfileActivity"
        const val EXTRA_USER_ID = "user_id"
    }
}
