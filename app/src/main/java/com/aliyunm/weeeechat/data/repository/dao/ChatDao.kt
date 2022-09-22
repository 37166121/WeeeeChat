package com.aliyunm.weeeechat.data.repository.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.aliyunm.weeeechat.data.model.ChatModel

@Dao
interface ChatDao {
    @Query("SELECT * FROM chat")
    fun getAllChat() : List<ChatModel>

    @Insert
    fun insertAll(vararg chat: ChatModel)

    @Query("DELETE FROM chat")
    fun deleteAll()

    @Delete
    fun delete(chat: ChatModel)
}