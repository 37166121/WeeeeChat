package com.aliyunm.weeeechat.util

import org.apache.commons.codec.binary.Hex
import java.security.Key
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

object AESUtil {

    val key : String

    init {
        key = generateKey()
    }

    /**
     * 生成key
     * @return
     */
    private fun generateKey(): String {
        try {
            val keyGenerator = KeyGenerator.getInstance("AES")
            keyGenerator.init(SecureRandom())
            val secretKey = keyGenerator.generateKey()
            val byteKey = secretKey.encoded
            return Hex.encodeHexString(byteKey)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * AES加密
     * @param thisKey
     * @param data
     * @return
     */
    fun encode(thisKey: String?, data: String): String? {
        try {
            // 转换KEY
            val key: Key = SecretKeySpec(Hex.decodeHex(thisKey), "AES")
            //System.out.println(thisKey);

            // 加密
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val result = cipher.doFinal(data.toByteArray())
            return Hex.encodeHexString(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    /**
     * AES解密
     * @param thisKey
     * @param data
     * @return
     */
    fun decode(thisKey: String?, data: String?): String? {
        try {
            // 转换KEY
            val key: Key = SecretKeySpec(Hex.decodeHex(thisKey), "AES")
            // 解密
            val cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
            cipher.init(Cipher.DECRYPT_MODE, key)
            val result = cipher.doFinal(Hex.decodeHex(data))
            return String(result)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}

// fun main() {
//     val s = AESUtil.encode(AESUtil.key, "ABCd")
//     println(AESUtil.decode(AESUtil.key, s))
// }