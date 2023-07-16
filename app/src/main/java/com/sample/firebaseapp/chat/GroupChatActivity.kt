package com.sample.firebaseapp.chat

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.View.OnLayoutChangeListener
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sample.firebaseapp.OnItemClickListener
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.chat.adapter.MessageListAdapter
import com.sample.firebaseapp.chat.adapter.MessageListReceiverViewHolder
import com.sample.firebaseapp.chat.adapter.MessageListSenderViewHolder
import com.sample.firebaseapp.databinding.ActivityGroupChatBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.model.UserModel

class GroupChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroupChatBinding

    private val viewModel: GroupChatViewModel by viewModels()

    private var adapter: MessageListAdapter? = null

    private var isFirstOpen: Boolean? = true

    private var messageListSenderViewHolder : MessageListSenderViewHolder? = null

    private var messageListReceiverViewHolder : MessageListReceiverViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupChatBinding.inflate(layoutInflater)

        binding.sendImageButton.setOnClickListener {
            sendMessage()
        }

        binding.messageListRecyclerView.addOnLayoutChangeListener(OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom <= oldBottom) {
                binding.messageListRecyclerView.postDelayed(
                    Runnable { binding.messageListRecyclerView.smoothScrollToPosition(bottom) },50
                )
            }
        })

        binding.messageEditText.onFocusChangeListener = object : OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                binding.messageListRecyclerView.post {
                    binding.messageListRecyclerView.layoutManager?.scrollToPosition((viewModel.getMessageList()?.size ?: 0) - 1)
                }
            }
        }



        getMessages()

        setContentView(binding.root)
    }

    private fun sendMessage() {
        viewModel.sendMessage(
            binding.messageEditText.text.toString().trim(),
            requestListener = object : RequestListener {

                override fun onSuccess() {
                    binding.messageEditText.text?.clear()
                }

                override fun onFailed(e: Exception) {

                }

            })
    }

    private fun getMessages() {
        viewModel.fetchMessageList(requestListener = object : RequestListener {
            override fun onSuccess() {
                setAdapter()
                isFirstOpen = false

                var clickedUserSurName = intent.getStringExtra("userSurname")
                var clickedUserEmail = intent.getStringExtra("userEmail")

                adapter!!.setOnItemClickListener(object : OnItemClickListener {
                    override fun onItemClick(position: Int) {

                        val intent = Intent(this@GroupChatActivity, UserProfileActivity::class.java)
                        intent.putExtra("userId", viewModel.getMessageList()?.get(position)?.userId)
                        intent.putExtra("userName", viewModel.getMessageList()?.get(position)?.userName)
                        intent.putExtra("userSurname", clickedUserSurName)
                        intent.putExtra("userEmail", clickedUserEmail)
                        startActivity(intent)


                    }
                })

            }

            override fun onFailed(e: Exception) {

            }
        })
    }

    private fun setAdapter() {
        if (isFirstOpen == false) {
            adapter?.updateData(viewModel.getMessageList())
            binding.messageListRecyclerView.scrollToPosition(
                (viewModel.getMessageList()?.count() ?: 0) - 1
            )
            return
        }

        adapter = MessageListAdapter(viewModel.getMessageList(), viewModel.getUserId())
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.messageListRecyclerView.layoutManager = layoutManager
        binding.messageListRecyclerView.adapter = adapter

    }

}