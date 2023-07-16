package com.sample.firebaseapp.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.databinding.ActivityOtherUsersProfileBinding
import com.sample.firebaseapp.helpers.FirebaseHelper


class OtherUsersProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore : FirebaseFirestore
    private lateinit var userEmail : String

    private lateinit var binding: ActivityOtherUsersProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOtherUsersProfileBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        auth = Firebase.auth
        firestore = Firebase.firestore

        val extras = intent.extras
        if (extras != null) {
            userEmail = extras.getString("userEmail", "")
        }


                getData()
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
                            val uid =document.get("userId") as String?



                            if (userEmail==email) {
                                getUserData(uid!!)
                                downloadUrl = document.get("downloadUrl") as String
                                if (downloadUrl != null) {
                                    Glide.with(this)
                                        .load(downloadUrl)
                                        .into(binding.imageView2)

                                } else {

                                }

                            }



                        }
                    }

                }
            }

        }
    }
    private fun getUserData(uid : String){

        FirebaseHelper.getUserModel(uid) { userModel ->
            binding.textViewEmail.text="Email : " + userModel?.email
            binding.textViewName.text="Name : " + userModel?.name
            binding.textViewSurname.text="Surname : " + userModel?.surName
}
    }
}