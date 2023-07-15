package com.sample.firebaseapp.chat.adapter

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sample.firebaseapp.R
import com.sample.firebaseapp.databinding.LayoutMessageSenderBinding
import com.sample.firebaseapp.helpers.FirebaseHelper
import com.sample.firebaseapp.model.MessageModel
import com.sample.firebaseapp.ui.profile.ProfileActivity

class MessageListSenderViewHolder(var binding: LayoutMessageSenderBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(model: MessageModel?) {
        binding.messageTextView.text = model?.message
        binding.userNameTextView.text = model?.userName
        FirebaseHelper.getUserPhotoUrl(model?.userId.toString()) { photoUrl ->
            if (photoUrl != null) {
                Glide.with(binding.root.context)
                    .load(photoUrl)
                    .placeholder(R.drawable.ic_default_profile_photo)
                    .into(binding.profilePhotoImageView)
            }
        }

        binding.userNameTextView.setOnClickListener {
            val intent = Intent(binding.root.context, ProfileActivity::class.java)
            intent.putExtra("userId", model?.userId)
            binding.root.context.startActivity(intent)
        }

    }
}