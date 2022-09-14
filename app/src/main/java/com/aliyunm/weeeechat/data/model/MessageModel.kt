package com.aliyunm.weeeechat.data.model

import java.util.Date

data class MessageModel<T>(
    /**
     * 消息类型
     */
    val type: Int = PRIVATE,

    /**
     * 消息内容
     */
    val content: T,

    /**
     * 消息发布时间
     */
    val time: Long = Date().time
) {

    companion object {
        // 消息类型
        /**
         * 连接到服务器
         */
        const val CONNECT = 0x0001

        /**
         * 断开连接
         */
        const val OFFLINE = 0x0002

        /**
         * 广播消息
         */
        const val BROADCAST = 0xf001

        /**
         * 区域广播
         */
        const val SPECIFY = 0xf002

        /**
         * 私有消息
         */
        const val PRIVATE = 0xf003

        /**
         * 进入房间
         */
        const val ENTER_ROOM = 0xff01

        /**
         * 退出房间
         */
        const val QUIT_ROOM = 0xff02
    }
}