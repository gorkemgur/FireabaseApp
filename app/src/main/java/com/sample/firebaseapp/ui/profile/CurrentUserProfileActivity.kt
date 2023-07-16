package com.sample.firebaseapp.ui.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.sample.firebaseapp.databinding.ActivityCurrentUserProfileBinding
import java.util.*
import com.sample.firebaseapp.helpers.FirebaseHelper


class CurrentUserProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var storage : FirebaseStorage
    private lateinit var activityResaultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>
    var selectedPicture : Uri? = null

    private lateinit var binding: ActivityCurrentUserProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCurrentUserProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.button.visibility=View.GONE


        auth = Firebase.auth
        firestore = Firebase.firestore
        storage = Firebase.storage

        registerLauncher()
        getData()

            FirebaseHelper.getCurrentUserModel { userModel ->
                if (userModel != null) {
                    binding.textViewEmail.text="Email : " + userModel.email
                    binding.textViewName.text="Name : " + userModel.name
                    binding.textViewSurname.text="Surname : " + userModel.surName

                } else {
                    Toast.makeText(this,"Login to see user information",Toast.LENGTH_LONG).show()

                 }


        }

        binding.button.setOnClickListener {
            upload()
        }

    }
    fun selectImage(view: View) {

        if (ContextCompat.checkSelfPermission(this,  android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                Snackbar.make(view, "Permission needed for gallery", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Give Permission") {
                        permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)

                    }.show()


            } else {

                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }

        }else
        {

            val intentToGallery = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResaultLauncher.launch(intentToGallery)
        }
    }

    private fun registerLauncher(){
        activityResaultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                result ->
            if (result.resultCode == RESULT_OK){
                val intentFromResult = result.data
                if (intentFromResult != null){
                    selectedPicture = intentFromResult.data

                    selectedPicture?.let {
                        binding.imageView2.setImageURI(it)
                        binding.button.visibility = View.VISIBLE
                    }
                }
            }
        }
        permissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
                result ->
            if (result){
                val intentToGallery = Intent(Intent.ACTION_PICK,MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                activityResaultLauncher.launch(intentToGallery)
                binding.button.visibility=View.VISIBLE
            }
            else{
                Toast.makeText(this,"Permission needed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun upload() {

        val uuid = UUID.randomUUID().toString()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)


        if(selectedPicture!=null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener{

                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl = it.toString()

                    val postMap = hashMapOf<String,Any>()
                    postMap.put("downloadUrl",downloadUrl)
                    postMap.put("userId",auth.currentUser!!.uid)
                    postMap.put("userEmail",auth.currentUser!!.email!!)
                    postMap.put("date",Timestamp.now())
                    firestore.collection("Profiles").add(postMap).addOnSuccessListener {
                        binding.button.visibility=View.GONE
                    }.addOnFailureListener {
                        Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
                    }
                }


            }.addOnFailureListener{
                Toast.makeText(this,it.localizedMessage,Toast.LENGTH_LONG).show()
            }
        }


    }

    private fun getData(){

         var downloadUrl=""
        firestore.collection("Profiles").orderBy("date",Query.Direction.ASCENDING).addSnapshotListener{ value, error ->
            if (error != null){
                Toast.makeText(this,error.localizedMessage, Toast.LENGTH_LONG).show()
            }
            else{
                if (value != null){
                    if (!value.isEmpty){

                        val documents = value.documents
                        for (document in documents){

                            val email = document.get("userEmail") as String?

                            if (auth.currentUser!!.email==email) {

                                downloadUrl = document.get("downloadUrl") as String
                                if (downloadUrl != null) {
                                    if (!isDestroyed && !isFinishing) {
                                        Glide.with(this)
                                            .load(downloadUrl)
                                            .into(binding.imageView2)
                                    }

                                } else {

                                }

                            }



                        }
                    }

                }
            }

        }
    }




}

