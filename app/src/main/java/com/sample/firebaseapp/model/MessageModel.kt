package com.sample.firebaseapp.model

data class MessageModel(
    var userName: String?,
    var userId: String?,
    var message: String?,
    var messageTime: String?
) {
    constructor(): this("","","","")
}
