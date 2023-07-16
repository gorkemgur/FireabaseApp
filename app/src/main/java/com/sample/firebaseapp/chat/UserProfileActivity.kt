package com.sample.firebaseapp.chat

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage

import com.sample.firebaseapp.databinding.ActivityUserProfileBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.model.UserModel
import com.squareup.picasso.Picasso
import java.util.UUID
class UserProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUserProfileBinding
    private lateinit var activityResultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher: ActivityResultLauncher<String>
    var selectedPicture : Uri? = null
    private lateinit var auth : FirebaseAuth
    private lateinit var database : FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private var userModel: UserModel? = null

    init {
        FirebaseHelper.getCurrentUserModel {
            userModel = it
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserProfileBinding.inflate(layoutInflater)
        val view = binding.root

        binding.nameTextView.text = "${intent.getStringExtra("userName")}" + " " + "${intent.getStringExtra("userSurname")}"
        binding.emailTextView.text = "${intent.getStringExtra("userEmail")}"

        registerLauncher()

        binding.imageView.setOnClickListener {
            selectImage(view)
        }

        binding.button.setOnClickListener{
            setImage(view)
        }




        Picasso.get().load(userModel?.imageUrl).into(binding.imageView)
        setContentView(view)



        auth = Firebase.auth
        database = Firebase.database
        storage = Firebase.storage





    }

    fun selectImage(view : View) {

        if (ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(view,"Galeri için izin gerekli!",Snackbar.LENGTH_INDEFINITE).setAction("İzin ver") {
                    //request permission
                    permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }.show()
            }else {
                //request permission
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }else {
            val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            //start activity for result
            activityResultLauncher.launch(intentToGallery)
        }
    }

    fun setImage(view: View) {

        val uuid = UUID.randomUUID()
        val imageName = "$uuid.jpg "

        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if (selectedPicture != null) {
            imageReference.putFile(selectedPicture!!).addOnSuccessListener {
                //download url -> rt database
                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()

                    updateUser(downloadUrl)

                }

            }.addOnFailureListener {
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }
    }
    fun updateUser(downloadUrl: String) {

        val updateData : DatabaseReference
        val currentUser = auth.currentUser

        updateData = FirebaseDatabase.getInstance().getReference("Users").child(currentUser!!.uid)

        updateData.child("imageUrl").setValue(downloadUrl)
    }



    private fun registerLauncher() {

        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val intentFromResult = result.data
                if(intentFromResult != null) {
                    selectedPicture = intentFromResult.data
                    selectedPicture.let {
                        binding.imageView.setImageURI(it)
                    }
                }
            }
        }

        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { result ->
            if(result) {
                //permission granted
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResultLauncher.launch(intentToGallery)

            }else {
                //permission denied
                Toast.makeText(this@UserProfileActivity,"İzin gerekli!",Toast.LENGTH_LONG).show()
            }
        }
    }

}
