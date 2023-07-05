package com.sample.firebaseapp.ui.common

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View

import android.widget.TextView
import androidx.appcompat.app.AlertDialog

import androidx.appcompat.app.AppCompatActivity
import com.sample.firebaseapp.R

abstract class BaseActivity : AppCompatActivity() {
    var alertDialogProgressBar: AlertDialog? = null

    open fun showLoadingProgressBar(message: String?) {
        val adb: AlertDialog.Builder = AlertDialog.Builder(this)
        val view: View = layoutInflater.inflate(R.layout.progress_bar, null)
        val textView: TextView = view.findViewById<TextView>(R.id.loading_text_view)
        textView.setText(message)
        adb.setView(view)
        alertDialogProgressBar = adb.create()
        alertDialogProgressBar?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        alertDialogProgressBar?.setCancelable(false)
        alertDialogProgressBar?.show()
    }

    open fun dismissProgressBar() {
        if (alertDialogProgressBar == null) {
            return
        }
        alertDialogProgressBar?.dismiss()
    }
}
