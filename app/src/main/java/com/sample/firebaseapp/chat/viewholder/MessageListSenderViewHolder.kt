package com.sample.firebaseapp.chat.viewholder

import android.content.Intent
import android.os.Message
import android.view.View
import android.view.View.OnLongClickListener
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.core.Context
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.profile.ProfileActivity

class MessageListSenderViewHolder(var binding: LayoutMessageSenderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?,context:android.content.Context) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName

        binding.userNameTextView.setOnClickListener {
            val userId = model?.userId ?: ""
            val intent = Intent(context, ProfileActivity::class.java).apply {
                putExtra(ProfileActivity.EXTRA_USER_ID, userId)
            }
            context.startActivity(intent)
        }
    }
}