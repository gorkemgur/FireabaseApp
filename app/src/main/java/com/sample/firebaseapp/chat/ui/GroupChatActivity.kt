package com.sample.firebaseapp.chat.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnLayoutChangeListener
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.chat.adapter.MessageListAdapter
import com.sample.firebaseapp.chat.adapter.MessageListener
import com.sample.firebaseapp.databinding.ActivityGroupChatBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.ui.common.profile.ProfileActivity

class GroupChatActivity : AppCompatActivity() {

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
                        binding.messageListRecyclerView.layoutManager?.scrollToPosition(
                            (viewModel.getMessageList()?.size ?: 0) - 1
                        )
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
            messageListener
        )
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.messageListRecyclerView.layoutManager = layoutManager
        binding.messageListRecyclerView.adapter = adapter

    }

    private val messageListener = object : MessageListener {
        override fun onLongClicked(messageModel: MessageModel?) {
            AlertDialog.Builder(this@GroupChatActivity)
                .setTitle("Mesaj silinecek Onaylıyor Musun ?")
                .setMessage("Bu Mesajı ${messageModel?.message}")
                .setPositiveButton("Sil") { dialog, _ ->
                    viewModel.setClickedMessageModel(messageModel)
                    deleteMessage()
                    dialog.dismiss()
                }
                .setNegativeButton("Vazgeç") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        override fun onUserNameClicked(userId: String?) {
            FirebaseHelper.getUserModelWithUserId(userId) { userModel ->
                val intent = Intent(this@GroupChatActivity, ProfileActivity::class.java)
                intent.putExtra("userId", userModel?.userId)
                startActivity(intent)
            }
        }
    }

    private fun deleteMessage() {
        viewModel.removeMessage(object : RequestListener {
            override fun onSuccess() {
                adapter?.updateData(viewModel.getMessageList())
            }

            override fun onFailed(e: java.lang.Exception) {
                Toast.makeText(this@GroupChatActivity, e.localizedMessage, Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }


    private fun updateAdapter() {
        adapter?.updateData(viewModel.getMessageList())
        binding.messageListRecyclerView.scrollToPosition(
            (viewModel.getMessageList()?.count() ?: 0) - 1
        )
        return
    }

}