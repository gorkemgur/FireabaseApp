package com.sample.firebaseapp.ui.profile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import java.lang.Exception


class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var storage: FirebaseStorage
    private lateinit var activityResaultLauncher: ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture: Uri? = null
    private var uid: String? = null
    private lateinit var viewModel: ProfileActivityViewModel
    private lateinit var binding: ActivityProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(ProfileActivityViewModel::class.java)

        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        binding.button.visibility = View.GONE


        val extras = intent.extras
        if (extras != null) {

            uid = extras.getString("userId", "")
            if (auth.currentUser?.uid == uid) {
                FirebaseHelper.getCurrentUserModel { userModel ->
                    if (userModel != null) {
                        binding.textViewEmail.text = "Email : " + userModel.email
                        binding.textViewName.text = "Name : " + userModel.name
                        binding.textViewSurname.text = "Surname : " + userModel.surName

                    } else {
                        Toast.makeText(this, "Login to see user information", Toast.LENGTH_LONG)
                            .show()

                    }
                }
            } else {
                binding.textView.visibility = View.GONE

                FirebaseHelper.getUserModel(uid!!) { userModel ->
                    if (userModel != null) {
                        println(userModel.email.toString())
                        binding.textViewEmail.text = "Email : " + userModel.email + "."
                        binding.textViewName.text = "Name : " + userModel.name
                        binding.textViewSurname.text = "Surname : " + userModel.surName

                    } else {
                        Toast.makeText(this, "Login to see user information", Toast.LENGTH_LONG)
                            .show()

                    }
                }

            }
        }

        registerLauncher()
        uid?.let { viewModel.initialize(it, activityResaultLauncher) }

        binding.imageView2.setOnClickListener {
            viewModel.selectImage(view, requestListener = object : RequestListener {
                override fun onSuccess() {
                    binding.button.visibility = View.VISIBLE
                }

                override fun onFailed(e: Exception) {
                    Toast.makeText(this@ProfileActivity, e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()

                }
            })
        }
        binding.button.setOnClickListener {
            //upload()
            viewModel.upload(selectedPicture!!, requestListener = object : RequestListener {
                override fun onSuccess() {
                    binding.button.visibility = View.GONE
                }

                override fun onFailed(e: Exception) {
                    Toast.makeText(this@ProfileActivity, e.localizedMessage, Toast.LENGTH_SHORT)
                        .show()
                }
            })

        }



        viewModel.downloadUrl.observe(this) { url ->
            if (url != null) {
                Glide.with(this)
                    .load(viewModel.downloadUrl.value)
                    .into(binding.imageView2)
            }
        }


    }

    private fun registerLauncher() {
        activityResaultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val intentFromResult = result.data
                    if (intentFromResult != null) {
                        selectedPicture = intentFromResult.data

                        selectedPicture?.let {
                            binding.imageView2.setImageURI(it)
                            binding.button.visibility = View.VISIBLE
                        }
                    }
                }
            }
        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
                if (result) {
                    val intentToGallery =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    activityResaultLauncher.launch(intentToGallery)
                    binding.button.visibility = View.VISIBLE
                } else {
                    Toast.makeText(this, "Permission needed!", Toast.LENGTH_SHORT).show()
                }
            }
    }
}