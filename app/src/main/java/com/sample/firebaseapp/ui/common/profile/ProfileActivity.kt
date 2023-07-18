package com.sample.firebaseapp.ui.common.profile


import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.helpers.GlideHelper
import com.sample.firebaseapp.ui.common.BaseActivity
import java.io.File
import java.io.IOException
import java.util.*


class ProfileActivity : BaseActivity() {

    companion object {
        private val IMAGE_CHOOSE = 1000;
        private val IMAGE_TAKE_CAMERA = 1002;
        private val PERMISSION_CODE = 1001;
    }

    private lateinit var binding: ActivityProfileBinding

    private val viewModel: ProfileViewModel by viewModels()

    private var file: File? = null

    private var fileUri: Uri? = null

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
        binding.userTitle.text = viewModel.getUserName()
        binding.userEmailTextView.text = viewModel.getUserEmail()

        binding.backButtonImageView.setOnClickListener {
            finish()
        }

        GlideHelper.loadImage(
            this@ProfileActivity,
            binding.profileImageView,
            viewModel.getUserImageUrl()
        )

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

        binding.cameraPhotoTake.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    requestPermissions(camera_permission, PERMISSION_CODE)
                } else {
                    chooseImageCamera();
                }
            } else {
                chooseImageCamera();
            }
        }

        if (viewModel.isCurrentUser() == true) {
            binding.optionsMenu.visibility = View.VISIBLE

            binding.saveButton.setOnClickListener {
                uploadImageDatabase()
            }
        }
    }

    private fun chooseImageCamera() {
        val imageFileName = "takenPhoto_${UUID.randomUUID()}" //make a better file name

        val storageDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)

        file = File.createTempFile(
            imageFileName,
            ".jpg",
            storageDir
        )

        fileUri = file?.let {
            FileProvider.getUriForFile(
                this@ProfileActivity,
                "${packageName}.provider",  //(use your app signature + ".provider" )
                it
            )
        }

        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri)
        startActivityForResult(takePhotoIntent, IMAGE_TAKE_CAMERA)
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
        } else if (requestCode == IMAGE_TAKE_CAMERA && resultCode == Activity.RESULT_OK) {
            fileUri?.let { uri ->
                viewModel.setImageUri(uri)
                binding.profileImageView.setImageURI(uri)
            }
        }
    }

    private fun uploadImageDatabase() {
        showLoadingProgressBar("Lütfen Bekleyin")
        viewModel.uploadUserProfileImage(object : RequestListener {
            override fun onSuccess() {
                Toast.makeText(
                    this@ProfileActivity,
                    "Fotoğraf Başarıyla Kaydedildi",
                    Toast.LENGTH_SHORT
                ).show()
                dismissProgressBar()
            }

            override fun onFailed(e: Exception) {
                Toast.makeText(this@ProfileActivity, e.localizedMessage, Toast.LENGTH_LONG)
                    .show()
            }
        })
    }

    var camera_permission = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA
    )

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