package com.sample.firebaseapp.chat.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
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

    private var clickedMessageModel: MessageModel? = null

    init {
        FirebaseHelper.getCurrentUserModel {
            userModel = it
        }
    }

    fun sendMessage(message: String?, requestListener: RequestListener) {
        val key = databaseReference.child("GroupChats").push().key
        val messageModel =
            MessageModel(userModel?.name, userModel?.userId, message, getCurrentTime(), key, false)
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
                messageList?.clear()
                for (dsp in snapshot.children) {
                    if (dsp.value != null) {
                        val message = dsp.getValue(MessageModel::class.java)
                        message?.let {
                            messageList?.add(message)
                        }
                        requestListener.onSuccess()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                requestListener.onFailed(Exception(error.toException()))
            }
        })
    }

    fun removeMessage(requestListener: RequestListener) {
        databaseReference.child("GroupChats").child(clickedMessageModel?.messageId ?: "")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageIndex = messageList?.indexOfFirst { it == clickedMessageModel }
                    snapshot.ref.child("deleted").setValue(true).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            messageList?.get(messageIndex ?: 0)?.isDeleted = true
                            requestListener.onSuccess()
                        } else {
                            task.exception?.let { requestListener.onFailed(it) }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    requestListener.onFailed(error.toException())
                }

            })
    }

    fun setClickedMessageModel(messageModel: MessageModel?) {
        clickedMessageModel = messageModel
    }

    fun getSelectedMessageModel(): MessageModel? {
        return clickedMessageModel
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
}