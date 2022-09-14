package com.aliyunm.weeeechat.data.model

import com.aliyunm.weeeechat.util.SharedPreferencesUtil
import java.io.Serializable
import java.util.UUID

data class UserModel(
    val uid : String = SharedPreferencesUtil.getString("uid", UUID.randomUUID().toString()),
    val nickname : String = SharedPreferencesUtil.getString("nickname", ""),
    val publicKey : String = ""
) : Serializable {
}