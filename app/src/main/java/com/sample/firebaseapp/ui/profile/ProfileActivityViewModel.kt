package com.sample.firebaseapp.ui.profile

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
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
import com.sample.firebaseapp.RequestListener
import java.lang.Exception
import java.util.*

class ProfileActivityViewModel : ViewModel(){

    private val auth: FirebaseAuth = Firebase.auth
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val storage: FirebaseStorage = Firebase.storage

    private val _downloadUrl: MutableLiveData<String> = MutableLiveData()
    val downloadUrl: LiveData<String>
        get() = _downloadUrl

    private lateinit var activityResaultLauncher : ActivityResultLauncher<Intent>
    private lateinit var permissionLauncher : ActivityResultLauncher<String>

    lateinit var uid: String

    fun initialize(uid: String, activityResultLauncher: ActivityResultLauncher<Intent>) {
        this.uid = uid
        this.activityResaultLauncher=activityResultLauncher
        getData()
    }



fun selectImage(view: View,requestListener: RequestListener) {

    if (auth.currentUser?.uid==uid){
        if (ContextCompat.checkSelfPermission(view.context,  android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    view.context as AppCompatActivity,
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
    }else{
        requestListener.onFailed(e = Exception())
    }
}


    private fun getData(){

        firestore.collection("Profiles").orderBy("date",Query.Direction.ASCENDING).addSnapshotListener{ value, error ->
            if (error != null){

            }
            else{
                if (value != null){
                    if (!value.isEmpty){

                        val documents = value.documents
                        for (document in documents){

                            val uid2 = document.get("userId") as String?
                            if (uid==uid2) {
                                val url = document.get("downloadUrl") as String?
                                if (url != null) {
                                    _downloadUrl.value = url!!
                                }
                               println(downloadUrl)
                            } else {

                                }
                            }
                        }
                    }
                }
            }
        }


    fun upload(selectedPicture : Uri?,requestListener: RequestListener) {

        val uuid = UUID.randomUUID().toString()
        val imageName = "$uuid.jpg"

        val reference = storage.reference
        val imageReference = reference.child("images").child(imageName)

        if(selectedPicture!=null){
            imageReference.putFile(selectedPicture!!).addOnSuccessListener{

                val uploadPictureReference = storage.reference.child("images").child(imageName)
                uploadPictureReference.downloadUrl.addOnSuccessListener {
                    val downloadUrl2 = it.toString()

                    val postMap = hashMapOf<String,Any>()
                    postMap.put("downloadUrl",downloadUrl2)
                    postMap.put("userId",auth.currentUser!!.uid)
                    postMap.put("userEmail",auth.currentUser!!.email!!)
                    postMap.put("date",Timestamp.now())
                    firestore.collection("Profiles").add(postMap).addOnSuccessListener{
                        requestListener.onSuccess()

                    }.addOnFailureListener {
                        requestListener.onFailed(it)
                    }
                }

            }.addOnFailureListener{
                requestListener.onFailed(it)
            }
        }
    }


}



