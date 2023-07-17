package com.sample.firebaseapp.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.sample.firebaseapp.dashboard.DashboardViewModel
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import java.util.UUID

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding

    private lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    private var selectedPicture: Uri? = null
    private lateinit var storage : FirebaseStorage
    private var databaseReference: DatabaseReference = Firebase.database.reference
    private val viewModel : DashboardViewModel by viewModels()
    private var authentication: FirebaseAuth = Firebase.auth

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileBinding.inflate(layoutInflater)

        authentication = Firebase.auth
        storage = Firebase.storage

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

        binding.textEmail.text = viewModel.getEmail()
        binding.textName.text = viewModel.getUserName()

        setContentView(binding.root)

        registerLauncher()
    }

    private fun uploadImage() {

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("images/$imageName")

        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()
                    TODO("Get the url and associate with the user")
                }
            }.addOnFailureListener {
                Toast.makeText(this, "Yükleme başarısız", Toast.LENGTH_SHORT).show()
            }
        }
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