package com.sample.firebaseapp.chat.viewholder

import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.sample.firebaseapp.databinding.LayoutMessageReceiverBinding
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.model.UserModel
import com.sample.firebaseapp.profile.ProfileActivity

class MessageListReceiverViewHolder(var binding: LayoutMessageReceiverBinding): RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?, context: Context) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName

        binding.userNameTextView.setOnClickListener {
            val userId = model?.userId ?: ""
            val intent = Intent(context,ProfileActivity::class.java).apply {
                putExtra(ProfileActivity.EXTRA_USER_ID, userId)
            }
            context.startActivity(intent)
        }
    }

}