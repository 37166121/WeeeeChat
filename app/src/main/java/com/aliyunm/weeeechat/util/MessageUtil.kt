package com.aliyunm.weeeechat.util

import java.lang.Exception
import java.util.Base64

/**
 * 消息加密、解密工具
 */
object MessageUtil {
    /**
     * 分隔符
     */
    private const val s = "--------------------------"

    /**
     * 加密
     * @param message       消息
     * @param serverPublicKey 服务器发布的公钥
     * @return 密文
     */
    fun encrypt(message: String, serverPublicKey: String): String {
        return try {
            // AES加密消息
            val aesEncrypt = AESUtil.encode(AESUtil.key, message)
            // 拿服务器公钥
            val publicKey = RSAUtil.generatePublicKey(Base64.getDecoder().decode(serverPublicKey))
            // RSA加密AES密钥
            val rsaEncrypt = RSAUtil.encrypt(AESUtil.key.toByteArray(), publicKey)
            // RSA密文转base64
            val rsa = String(Base64.getEncoder().encode(rsaEncrypt))

            "$aesEncrypt$s$rsa$s\n"
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    /**
     * 解密
     * @param aesCipherText AES加密的内容
     * @param rsaCipherText RSA加密的内容(AES密钥)
     * @return json明文
     */
    fun decode(aesCipherText: String, rsaCipherText: String): String {
        return try {
            // 拿私钥
            val privateKey = RSAUtil.generatePrivateKey(RSAUtil.getPrivate())
            // 私钥解密出AES密钥
            val aesKey = String(RSAUtil.decrypt(Base64Utils.decode(rsaCipherText.toByteArray()),privateKey))
            // AES密钥解密出消息
            AESUtil.decode(aesKey, aesCipherText) ?: ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }
}