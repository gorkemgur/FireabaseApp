package com.sample.firebaseapp.ui.common.profile


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.ui.common.BaseActivity
import java.io.IOException


class ProfileActivity : BaseActivity() {

    companion object {
        private val IMAGE_CHOOSE = 1000;
        private val IMAGE_TAKE = 1002;
        private val PERMISSION_CODE = 1001;
    }

    private lateinit var binding: ActivityProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)

        showLoadingProgressBar("Lütfen Bekleyin")
        intent?.let { it ->
            if (it.hasExtra("userId")) {
                FirebaseHelper.getUserModelWithUserId(userId = it.extras?.getString("userId")) { userModel ->
                    viewModel.setUserModel(userModel)
                    dismissProgressBar()
                    loadUI()
                }
            }
        }

        setContentView(binding.root)
    }

    private fun loadUI() {
        binding.userNameTextView.text = viewModel.getUserName()
        binding.userEmailTextView.text = viewModel.getUserEmail()

        Glide.with(this@ProfileActivity)
            .load(viewModel.getUserImageUrl())
            .placeholder(com.sample.firebaseapp.R.drawable.ic_profile)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(binding.profileImageView)

        if (viewModel.isCurrentUser() == true) {
            binding.galleryPhotoSelect.visibility = View.VISIBLE
            binding.galleryPhotoSelect.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                        permissions()?.let { it1 -> requestPermissions(it1, PERMISSION_CODE) }
                    } else {
                        chooseImageGallery();
                    }
                } else {
                    chooseImageGallery();
                }
            }

            binding.cameraPhotoTake.visibility = View.VISIBLE

            binding.cameraPhotoTake.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                        permissions()?.let { it1 -> requestPermissions(it1, PERMISSION_CODE) }
                    } else {
                        chooseImageCamera();
                    }
                } else {
                    chooseImageCamera();
                }
            }

            binding.saveButton.setOnClickListener {
                uploadImageDatabase()
            }
        }
    }

    private fun chooseImageCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, IMAGE_TAKE)
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    chooseImageGallery()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_CHOOSE && resultCode == Activity.RESULT_OK) {
            val uri = data?.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uri)
                uri?.let { viewModel.setImageUri(it) }
                binding.profileImageView.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else if (requestCode == IMAGE_TAKE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.extras != null) {
                data.data?.let { uri ->
                    viewModel.setImageUri(uri)
                }
                binding.profileImageView.setImageBitmap(data.extras?.get("data") as? Bitmap)
            }
        }
    }

    private fun uploadImageDatabase() {
        showLoadingProgressBar("Lütfen Bekleyin")
        viewModel.uploadUserProfileImage(object : RequestListener {
            override fun onSuccess() {
                dismissProgressBar()
            }

            override fun onFailed(e: Exception) {
                Toast.makeText(this@ProfileActivity, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        })
    }

    var storage_permissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    var storage_permissions_33 = arrayOf(
        Manifest.permission.READ_MEDIA_IMAGES,
        Manifest.permission.READ_MEDIA_AUDIO,
        Manifest.permission.READ_MEDIA_VIDEO
    )

    fun permissions(): Array<String>? {
        val p: Array<String> = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            storage_permissions_33
        } else {
            storage_permissions
        }
        return p
    }
}