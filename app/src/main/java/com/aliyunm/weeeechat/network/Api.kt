package com.aliyunm.weeeechat.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Path

interface Api {

    companion object {
        const val DEBUG_IP = "192.168.2.82"
        const val RELEASE_IP = "47.242.160.196"

        const val SOCKET_PORT = 9999

        const val DEBUG_PORT = 58574
        const val DEBUG_BASEURL = "http://$DEBUG_IP:$DEBUG_PORT"

        const val RELEASE_PORT = 58574
        const val RELEASE_BASEURL = "http://$RELEASE_IP:$RELEASE_PORT"

        const val SOCKET = "/s/"
        const val GET_PUBLIC_KEY = "getPublicKey/"

        const val ROOM = "/room/"
        const val GET_ROOM = "getRoom/{rid}"
    }

    @GET("$SOCKET$GET_PUBLIC_KEY")
    suspend fun getPublicKey() : ResponseBody

    @GET("$ROOM$GET_ROOM")
    suspend fun getRoom(@Path("rid") rid : Int) : ResponseBody
}