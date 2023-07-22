package com.sample.firebaseapp.model

data class MessageModel(

    var userName: String?,
    var userId: String?,
    var message: String?,
    var messageTime: String?,
    var isDeleted: Boolean?,
    var messageId: String?
) {
    constructor(): this("", "", "", "", false, "")
}
