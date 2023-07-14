package com.sample.firebaseapp.model

data class MessageModel(
    var userName: String?,
    var userId: String?,
    var message: String?,
    var messageTime: String?,
    var isDeleted: Boolean = false
) {
    constructor(): this("","","","",)
}
