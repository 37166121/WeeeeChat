package com.aliyunm.weeeechat.network

import com.aliyunm.weeeechat.network.ApiImpl.API

object Service {

    suspend fun getPublicKey(callback : (String) -> Unit) {
        val reader = API.getPublicKey().charStream()
        val publicKey = StringBuffer()
        reader.readLines().forEach {
            publicKey.append(it)
        }
        callback(publicKey.toString())
    }
}