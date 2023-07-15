package com.sample.firebaseapp

import android.view.View
import com.sample.firebaseapp.model.MessageModel

interface LongPressed {
    fun popUpMenu(view : View, message : MessageModel?)
}