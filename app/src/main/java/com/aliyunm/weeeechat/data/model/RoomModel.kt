package com.aliyunm.weeeechat.data.model

data class RoomModel(
    /**
     * 房间id
     */
    val rid : Int,
    /**
     * 房间名称
     */
    val name : String,
    /**
     * 房间在线人数
     */
    val count : Int = 0,
    /**
     * 房间类型
     */
    val type : Int = ROOM,
    /**
     * 聊天记录
     */
    val messages : ArrayList<ChatModel> = arrayListOf(),
    /**
     * 在房间里面的用户
     */
    val users : ArrayList<UserModel> = arrayListOf()
) {
    companion object {
        /**
         * 房间
         */
        const val ROOM = 0x0001

        /**
         * 私聊房间
         */
        const val PRIVATE = 0x0002
    }
}