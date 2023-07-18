package com.sample.firebaseapp.extension

import android.R
import android.app.Activity
import androidx.swiperefreshlayout.widget.CircularProgressDrawable


fun Activity.createCircularProgress(): CircularProgressDrawable {
    val drawable = CircularProgressDrawable(this)
    drawable.setColorSchemeColors(
        R.color.black,
        R.color.white
    )
    drawable.centerRadius = 30f
    drawable.strokeWidth = 5f
    // set all other properties as you would see fit and start it
    // set all other properties as you would see fit and start it
    drawable.start()
    return drawable
}