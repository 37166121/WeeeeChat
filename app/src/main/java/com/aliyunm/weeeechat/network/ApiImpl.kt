package com.aliyunm.weeeechat.network

import android.util.Log
import com.aliyunm.weeeechat.BuildConfig
import com.aliyunm.weeeechat.network.Api.Companion.DEBUG_BASEURL
import com.aliyunm.weeeechat.network.Api.Companion.RELEASE_BASEURL
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


/**
 * http请求服务
 */
object ApiImpl {

    /**
     * 配置Retrofit
     */
    val API : Api by lazy {
        val client = getOkHttpClient()
        Retrofit.Builder()
            .baseUrl(if (BuildConfig.DEBUG) { DEBUG_BASEURL } else { RELEASE_BASEURL })
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
            .create(Api::class.java)
    }

    /**
     * 配置okhttp
     */
    private fun getOkHttpClient(): OkHttpClient {
        val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            .addInterceptor(HttpInterceptor)
            .retryOnConnectionFailure(true)
            .cookieJar(CookieJarManage)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .connectTimeout(10, TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor = HttpLoggingInterceptor()
            builder.addInterceptor(httpLoggingInterceptor.apply {
                httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            })
        }
        return builder.build()
    }

    /**
     * cookie管理器
     */
    private object CookieJarManage : CookieJar {
        private val cookieStore: HashMap<String, List<Cookie>> = HashMap()

        override fun loadForRequest(url: HttpUrl): List<Cookie> {
            val cookies = cookieStore[url.host]
            return cookies ?: ArrayList()
        }

        override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
            cookieStore[url.host] = cookies
        }

    }

    /**
     * 拦截器
     */
    private object HttpInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val response : Response = chain.proceed(request)
            Log.i("status code", response.code.toString())
            return response
        }
    }

}