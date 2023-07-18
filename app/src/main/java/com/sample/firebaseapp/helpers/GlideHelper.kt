package com.sample.firebaseapp.helpers

import android.content.Context
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sample.firebaseapp.R
import com.sample.firebaseapp.extension.createCircularProgress

object GlideHelper {
    fun loadImage(context: Context, intoView: ImageView, url: String) {
        Glide.with(context)
            .load(url)
            .placeholder((context as? AppCompatActivity)?.createCircularProgress())
            .error(R.drawable.ic_profile)
            .into(intoView)
    }
}