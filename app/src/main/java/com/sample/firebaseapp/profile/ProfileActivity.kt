package com.sample.firebaseapp.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.sample.firebaseapp.R
import com.sample.firebaseapp.databinding.ActivityProfileBinding
import com.sample.firebaseapp.model.UserModel
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var storage: FirebaseStorage
    private lateinit var userId: String
    private var selectedBitmap: Bitmap? = null

    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val imageBitmap = data?.extras?.get("data") as? Bitmap
                imageBitmap?.let { bitmap ->
                    selectedBitmap = bitmap
                    binding.profileImageView.setImageBitmap(selectedBitmap)

                }
            }
        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                data?.data?.let { uri ->
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        storage = Firebase.storage


        userId = intent.getStringExtra(EXTRA_USER_ID) ?: ""

        binding.addPhotoButton.isVisible = userId == auth.currentUser?.uid
        binding.savePhotoButton.isVisible = userId == auth.currentUser?.uid

        binding.addPhotoButton.setOnClickListener {
            selectImage()
        }

        binding.savePhotoButton.setOnClickListener {
          uploadProfilePicture(selectedBitmap!!)
        }

        fetchUserProfile()
    }

    private fun fetchUserProfile() {
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(userId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(UserModel::class.java)
                user?.let {
                    binding.emailTextView.text = user.email
                    binding.nameTextView.text = user.name!!.uppercase()
                    binding.surnameTextView.text = user.surName!!.uppercase()


                    user.imageUrl?.let { profilePictureUrl ->
                        Glide.with(this@ProfileActivity)
                            .load(profilePictureUrl)
                            .placeholder(R.drawable.baseline_account_box_24)
                            .error(R.drawable.baseline_account_box_24)
                            .into(binding.profileImageView)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e(TAG, "Failed to fetch user profile: $error")
            }
        })
    }


    private fun selectImage() {
        val options = arrayOf<CharSequence>("Kamera", "Galeri", "İptal")
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("Profil fotoğrafı seç")
        builder.setItems(options) { _, item ->
            when {
                options[item] == "Kamera" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    takePictureLauncher.launch(intent)
                }
                options[item] == "Galeri" -> {
                    val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/*"
                    pickImageLauncher.launch(intent)
                }
                options[item] == "İptal" -> {
                    builder.setCancelable(true)
                }
            }
        }
        builder.show()
    }

    private fun uploadProfilePicture(bitmap: Bitmap) {
        val storageRef = storage.reference
        val profilePictureRef = storageRef.child("profile_pictures/$userId")

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        val uploadTask = profilePictureRef.putBytes(data)
        uploadTask.addOnSuccessListener { uploadTaskSnapshot ->

            profilePictureRef.downloadUrl.addOnSuccessListener { uri ->
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri)
                    .build()

                auth.currentUser?.updateProfile(profileUpdates)
                    ?.addOnCompleteListener { task ->
                        if (task.isSuccessful) {

                            val userRef = FirebaseDatabase.getInstance().reference
                                .child("Users")
                                .child(userId)
                                .child("imageUrl")
                            userRef.setValue(uri.toString())
                                .addOnCompleteListener { updateTask ->
                                    if (updateTask.isSuccessful) {
                                        Toast.makeText(this, "Profil fotoğrafı değiştirildi", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Log.e(TAG, "Failed to update profile picture URL in the database")
                                    }
                                }
                        } else {
                            Log.e(TAG, "Failed to update profile picture in Firebase Authentication")
                        }
                    }
            }
        }.addOnFailureListener { exception ->

            Log.e(TAG, "Failed to upload profile picture: $exception")
        }
    }



    companion object {
        private const val TAG = "ProfileActivity"
        const val EXTRA_USER_ID = "user_id"
    }
}
