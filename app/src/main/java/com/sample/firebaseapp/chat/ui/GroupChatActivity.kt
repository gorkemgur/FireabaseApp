package com.sample.firebaseapp.chat.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.chat.adapter.MessageClickListener
import com.sample.firebaseapp.chat.adapter.MessageListAdapter
import com.sample.firebaseapp.chat.viewholder.MessageListReceiverViewHolder
import com.sample.firebaseapp.databinding.ActivityGroupChatBinding
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.profile.ProfileActivity

class GroupChatActivity : AppCompatActivity(), MessageClickListener, MessageDeleteListener, MessageListReceiverViewHolder.OnUserNameClickListener {

    private lateinit var binding: ActivityGroupChatBinding

    private val viewModel: GroupChatViewModel by viewModels()

    private var adapter: MessageListAdapter? = null

    private var isFirstOpen: Boolean? = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupChatBinding.inflate(layoutInflater)

        binding.sendImageButton.setOnClickListener {
            sendMessage()
        }

        binding.messageListRecyclerView.addOnLayoutChangeListener(OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom <= oldBottom) {
                binding.messageListRecyclerView.postDelayed(
                    Runnable { binding.messageListRecyclerView.smoothScrollToPosition(bottom) }, 50
                )
            }
        })

        binding.messageEditText.onFocusChangeListener = object : View.OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (hasFocus) {
                    binding.messageListRecyclerView.post {
                        binding.messageListRecyclerView.layoutManager?.scrollToPosition((viewModel.getMessageList()?.size ?: 0) - 1)
                    }
                }
            }
        }

        getMessages()

        setContentView(binding.root)
    }

    private fun sendMessage() {

        val textMessage = binding.messageEditText.text.toString().trim()

        if (textMessage.isNullOrEmpty()) {
            Toast.makeText(this@GroupChatActivity, "Boş Mesaj Gönderemezsin", Toast.LENGTH_SHORT)
                .show()
            return
        }

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
            }

            override fun onFailed(e: Exception) {

            }
        })
    }

    private fun setAdapter() {
        if (isFirstOpen == false) {
            updateAdapter()
        }

        adapter = MessageListAdapter(
            viewModel.getMessageList(),
            viewModel.getUserId(),
            this,
            this


        )
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.messageListRecyclerView.layoutManager = layoutManager
        binding.messageListRecyclerView.adapter = adapter

    }


    private fun updateAdapter() {
        adapter?.updateData(viewModel.getMessageList())
        binding.messageListRecyclerView.scrollToPosition(
            (viewModel.getMessageList()?.count() ?: 0) - 1
        )
        return
    }
    override fun onUserNameClick(userId: String) {
        val intent = Intent(this, ProfileActivity::class.java).apply {
            putExtra(ProfileActivity.EXTRA_USER_ID, userId)
        }
        startActivity(intent)
    }

    override fun showDeleteConfirmationDialog(message: MessageModel) {
        if (message.userId == viewModel.getUserId()) {
        AlertDialog.Builder(this)
            .setTitle("Mesajı Sil")
            .setMessage("Mesajı silmek istiyor musunuz?")
            .setPositiveButton("Sil") { dialog, _ ->
                viewModel.deleteMessage(message.messageId!!,message,this)
                adapter?.removeMessage(message)
                dialog.dismiss()
            }
            .setNegativeButton("İptal") { dialog, _ ->
                dialog.dismiss()
            }
            .show()
    }

    }

    override fun onMessageDeletedSuccessfully(message: MessageModel) {
        adapter?.removeMessage(message)
        Toast.makeText(this, "Mesaj silindi", Toast.LENGTH_SHORT).show()

    }

    override fun onMessageDeletionFailed(message: MessageModel, error: Exception) {

        Log.d("MessageListAdapter", "Failed to delete message: ${error.message}")}
    }
