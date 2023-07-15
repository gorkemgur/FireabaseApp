package com.sample.firebaseapp.model

data class MessageModel(
    var userName: String?,
    var userId: String?,
    var message: String?,
    var messageTime: String?
) {
    constructor(): this("","","","")

    fun toMap() : Map<String, Any?> {
        return mapOf(
            "userName" to userName,
            "userId" to userId,
            "message" to message,
            "messageTime" to messageTime,
        )
    }
}
