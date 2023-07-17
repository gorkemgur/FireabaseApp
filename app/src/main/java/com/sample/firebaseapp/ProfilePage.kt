package com.sample.firebaseapp

import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sample.firebaseapp.databinding.ActivityProfilePageBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.model.UserModel
import java.security.AccessController.getContext

class ProfilePage : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePageBinding
    private var imageUri: Uri = Uri.EMPTY
    private lateinit var storageReference: StorageReference;
    private lateinit var username:String
    var userModel: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setBackgroundColor(Color.TRANSPARENT)
        username = intent.getStringExtra("username").toString()
        binding.usernameOnProfileTextView.text = username
        storageReference = FirebaseStorage.getInstance().getReference("images/$username")

        FirebaseHelper.getCurrentUserModel {
            userModel = it
            val name = userModel?.name + " " + (userModel?.surName)
            binding.nameOnprofileTextview.text = name
        }

        binding.backButton.setOnClickListener() {
            finish()
        }



    }
    override fun onResume() {
        super.onResume()

        binding.editProfileimageButton.setOnClickListener() {
            editProfilePhoto()
        }
        storageReference = FirebaseStorage.getInstance().getReference("images/$username")

        storageReference.putFile(imageUri)
        FirebaseHelper.getCurrentUserModel {
            userModel = it
            Log.d("denemeuser3",userModel?.userId.toString())
            //Firebase Users icinde kullanıcı imageUrl güncelleme
            Firebase.database.getReference("Users").child(userModel?.userId.toString()).child("imageUrl").setValue(imageUri.toString())
            userModel?.imageUrl=imageUri.toString()
        }


        storageReference.downloadUrl.addOnSuccessListener(){ uri ->

            Glide
                .with(this)
                .load(uri)
                .centerCrop()
                .into(binding.profileImage);
            Log.d("ProfilePageTest",uri.toString())
        }.addOnFailureListener { exception ->
            Log.e("ProfilePageTest", "Resim indirme hatası: $exception")
        }
    }


    private val galleryLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                imageUri = it
                binding.profileImage.setImageURI(it)
            }
        }

    fun editProfilePhoto() {
        binding.editProfileimageButton.setOnClickListener() {
            galleryLauncher.launch("image/*")
        }
    }


}
