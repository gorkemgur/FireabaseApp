package com.sample.firebaseapp

interface RequestListener {
    fun onSuccess()

    fun onFailed(e: java.lang.Exception)
}