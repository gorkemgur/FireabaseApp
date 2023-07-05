package com.sample.firebaseapp.extension

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

fun View.hideKeyboard() = ViewCompat.getWindowInsetsController(this)
    ?.hide(WindowInsetsCompat.Type.ime())