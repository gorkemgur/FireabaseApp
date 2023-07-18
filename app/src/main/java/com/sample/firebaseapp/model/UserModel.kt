package com.sample.firebaseapp.model

import android.os.Parcelable


data class UserModel(
    var name: String?,
    var surName: String?,
    var userId: String?,
    var email: String?,
    var imageUrl: String?
) {
    constructor() : this("", "", "", "", "")
}
