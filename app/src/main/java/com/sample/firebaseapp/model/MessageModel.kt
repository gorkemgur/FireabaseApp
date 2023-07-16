package com.sample.firebaseapp.model


data class MessageModel(
    var messageId : String?,
    var userName: String?,
    var userId: String?,
    var message: String?,
    var messageTime: String?,
    var email: String?
) {

    constructor(): this("","","","","","")
}
