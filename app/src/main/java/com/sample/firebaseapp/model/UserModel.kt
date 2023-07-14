package com.sample.firebaseapp.model

data class UserModel(
    var name: String?,
    var surName: String?,
    var userId: String?,
    var userMail: String?,
    var imageUrl: String?
) {
    constructor() : this("", "", "","","")
}
