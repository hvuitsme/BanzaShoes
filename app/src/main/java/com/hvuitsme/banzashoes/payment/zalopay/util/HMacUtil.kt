package com.hvuitsme.banzashoes.payment.zalopay.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.nio.charset.StandardCharsets
import java.util.*
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

object HMacUtil {
    const val HMACMD5 = "HmacMD5"
    const val HMACSHA1 = "HmacSHA1"
    const val HMACSHA256 = "HmacSHA256"
    const val HMACSHA512 = "HmacSHA512"

    @RequiresApi(Build.VERSION_CODES.O)
    fun hMacBase64Encode(algorithm: String, key: String, data: String): String? {
        val bytes = hMacEncode(algorithm, key, data) ?: return null
        return Base64.getEncoder().encodeToString(bytes)
    }

    fun hMacHexStringEncode(algorithm: String, key: String, data: String): String? {
        val bytes = hMacEncode(algorithm, key, data) ?: return null
        return HexStringUtil.byteArrayToHexString(bytes)
    }

    private fun hMacEncode(algorithm: String, key: String, data: String): ByteArray? {
        return try {
            val mac = Mac.getInstance(algorithm)
            val signingKey = SecretKeySpec(key.toByteArray(StandardCharsets.UTF_8), algorithm)
            mac.init(signingKey)
            mac.doFinal(data.toByteArray(StandardCharsets.UTF_8))
        } catch (ex: Exception) {
            null
        }
    }
}