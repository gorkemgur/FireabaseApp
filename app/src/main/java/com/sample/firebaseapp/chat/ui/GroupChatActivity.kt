package com.sample.firebaseapp.chat.ui

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sample.firebaseapp.LongPressed
import com.sample.firebaseapp.R
import com.sample.firebaseapp.RequestListener
import com.sample.firebaseapp.chat.adapter.MessageListAdapter
import com.sample.firebaseapp.databinding.ActivityGroupChatBinding
import com.sample.firebaseapp.model.MessageModel

class GroupChatActivity : AppCompatActivity(), LongPressed {

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

        binding.messageListRecyclerView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            if (bottom <= oldBottom) {
                binding.messageListRecyclerView.postDelayed(
                    { binding.messageListRecyclerView.smoothScrollToPosition(bottom) }, 50
                )
            }
        }

        binding.messageEditText.onFocusChangeListener =
            View.OnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    binding.messageListRecyclerView.post {
                        binding.messageListRecyclerView.layoutManager?.scrollToPosition(
                            (viewModel.getMessageList()?.size ?: 0) - 1
                        )
                    }
                }
            }

        getMessages()

        setContentView(binding.root)
    }

    private fun sendMessage() {

        val textMessage = binding.messageEditText.text.toString().trim()

        if (textMessage.isEmpty()) {
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

    private fun deleteMessage(message: MessageModel?) {
        viewModel.deleteMessage(
            message,
            requestListener = object : RequestListener {
                override fun onSuccess() {
                    updateAdapter()
                }

                override fun onFailed(e: java.lang.Exception) {
                    Toast.makeText(this@GroupChatActivity, "Silinemedi", Toast.LENGTH_SHORT).show()
                }

            }
        )
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

    @RequiresApi(Build.VERSION_CODES.S)
    override fun popUpMenu(view: View, message: MessageModel?) {
        val popupMenu = PopupMenu(applicationContext, view)

        popupMenu.inflate(R.menu.pop_up_menu)
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.btn_delete -> {
                    deleteMessage(message)
                    true
                }

                else -> true
            }
        }

        popupMenu.show()

        val popup = PopupMenu::class.java.getDeclaredField("mPopup").apply {
            this.isAccessible = true
        }
        val menu = popup.get(popupMenu)
        menu.javaClass.apply {
            getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)
        }

    }

}