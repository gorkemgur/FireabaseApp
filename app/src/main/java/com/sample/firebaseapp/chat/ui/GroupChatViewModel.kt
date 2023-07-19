package com.sample.firebaseapp.chat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.model.UserModel
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class GroupChatViewModel(application: Application) : AndroidViewModel(application) {
    private val context = getApplication<Application>()

    private var databaseReference: DatabaseReference = Firebase.database.reference

    private var messageList: kotlin.collections.ArrayList<MessageModel>? = arrayListOf()

    private var userModel: UserModel? = null

    private var auth: FirebaseAuth ? = Firebase.auth



    init {
        FirebaseHelper.getCurrentUserModel {
            userModel = it
        }
    }



    fun sendMessage(message: String?, requestListener: RequestListener) {
        val key = databaseReference.child("GroupChats").push().key
        val messageModel =
            MessageModel(key,userModel?.name, userModel?.userId, message, getCurrentTime(),userModel?.email)
        key?.let { chatKey ->
            databaseReference.child("GroupChats").child(chatKey).setValue(messageModel)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        requestListener.onSuccess()
                    } else {
                        task.exception?.let { requestListener.onFailed(it) }
                    }
                }
        }
    }




    fun fetchMessageList(requestListener: RequestListener) {
        databaseReference.child("GroupChats").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (dsp in snapshot.children) {
                    if (dsp.value != null) {
                        val message = dsp.getValue(MessageModel::class.java)
                        message?.let { messageModel ->
                            if ((messageList?.contains(messageModel)) != true) {
                                messageList?.add(messageModel)
                            }
                        }
                    }
                }
                requestListener.onSuccess()
            }

            override fun onCancelled(error: DatabaseError) {
                requestListener.onFailed(Exception(error.toException()))
            }
        })
    }

    fun getMessageList(): ArrayList<MessageModel>? {
        return messageList
    }

    private fun getCurrentTime(): String? {
        val df: DateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
        return df.format(Calendar.getInstance().time)
    }

    fun getUserId(): String? {
        return userModel?.userId
    }


    fun deleteMessage(position: Int,requestListener: RequestListener) {

        val message = messageList?.get(position)
        val currentUserId =  auth!!.currentUser?.uid?: ""

        if (message?.userId == currentUserId) {
            val messageId = message.messageId
            val databaseReference = Firebase.database.reference
            databaseReference.child("GroupChats").child(messageId!!).removeValue().addOnSuccessListener {
                messageList!!.removeAt(position)
                requestListener.onSuccess()
            }.addOnFailureListener {
                requestListener.onFailed(e = java.lang.Exception())
            }
        }
    }




}