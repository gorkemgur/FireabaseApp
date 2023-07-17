package com.sample.firebaseapp

import android.graphics.Color
import android.net.Uri
import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.sample.firebaseapp.databinding.ActivityProfilePageBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.model.UserModel
import java.security.AccessController.getContext
import kotlin.properties.Delegates

class ProfilePage : AppCompatActivity() {
    private lateinit var binding: ActivityProfilePageBinding
    private var imageUri: Uri = Uri.EMPTY
    private lateinit var storageReference: StorageReference;
    private lateinit var username:String
    private var isOwner by Delegates.notNull<Boolean>()
    var userModel: UserModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfilePageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.backButton.setBackgroundColor(Color.TRANSPARENT)
        username = intent.getStringExtra("username").toString()
        isOwner=intent.getBooleanExtra("isOwner",false)
        binding.usernameOnProfileTextView.text = username
        storageReference = FirebaseStorage.getInstance().getReference("images/$username")

        FirebaseHelper.getCurrentUserModel {
            userModel = it

            val name = userModel?.name + " " + (userModel?.surName)
            binding.nameOnprofileTextview.text = name

            Log.d("denemeuser3",userModel?.userId.toString())
            //Firebase Users icinde kullanıcı imageUrl güncelleme
            Firebase.database.getReference("Users").child(userModel?.userId.toString()).child("imageUrl").setValue(imageUri.toString())
            userModel?.imageUrl=imageUri.toString()
        }

        binding.backButton.setOnClickListener() {
            finish()
        }




    }
    override fun onResume() {
        super.onResume()
        if (isOwner==true) {
            binding.editProfileimageButton.setOnClickListener() {
                editProfilePhoto()
            }
            storageReference = FirebaseStorage.getInstance().getReference("images/$username")

            storageReference.putFile(imageUri)
        }else{
            binding.editProfileimageButton.isVisible=false
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
