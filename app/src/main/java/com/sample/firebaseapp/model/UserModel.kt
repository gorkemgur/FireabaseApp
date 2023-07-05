package com.sample.firebaseapp.model

data class UserModel(
    var name: String?,
    var surName: String?
) {
    constructor(): this("","")
}
