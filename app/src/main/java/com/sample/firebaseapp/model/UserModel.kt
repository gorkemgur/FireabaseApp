package com.sample.firebaseapp.model

import android.provider.ContactsContract.CommonDataKinds.Email

data class UserModel(
    var name: String?,
    var surName: String?,
    var userId: String?,
    var imageUrl: String?,
    var email: String
) {
    constructor() : this("", "", "","","")
}
