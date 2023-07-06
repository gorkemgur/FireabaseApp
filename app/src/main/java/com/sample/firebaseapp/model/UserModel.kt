package com.sample.firebaseapp.model

data class UserModel(
    var name: String?,
    var surName: String?,
    var userId: String?
) {
    constructor() : this("", "", "")
}
