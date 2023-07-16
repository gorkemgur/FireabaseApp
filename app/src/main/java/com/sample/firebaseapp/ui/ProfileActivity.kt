package com.sample.firebaseapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null

    private var authentication: FirebaseAuth = Firebase.auth

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)



        binding.btnProfilePicture.setOnClickListener {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                ) {
                    Snackbar.make(it, "İzin gerekiyor", Snackbar.LENGTH_INDEFINITE)
                        .setAction("İzin ver") {
                            permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                        }.show()
                } else {
                    //request permission
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            } else {
                val intentToGallery =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                //start activity for result
                activityResultLauncher.launch(intentToGallery)
            }
        }

        setContentView(binding.root)

        registerLauncher()
    }

    private fun registerLauncher() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data
                        selectedPicture?.let {
                            binding.btnProfilePicture.setImageURI(it)
                        }
                    }
                }
            }
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    //permission granted
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResultLauncher.launch(intentToGallery)
                } else {
                    //permission denied
                    Toast.makeText(this, "Galeriye erişim kısıtlandı.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}