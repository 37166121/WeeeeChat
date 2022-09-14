package com.aliyunm.weeeechat.data.model

data class RoomModel(
    val rid : Int,
    val name : String,
    val messages : ArrayList<ChatModel> = ArrayList()
) {
}