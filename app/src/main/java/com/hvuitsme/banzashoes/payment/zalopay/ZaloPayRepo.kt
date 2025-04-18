package com.hvuitsme.banzashoes.payment.zalopay

import android.os.Build
import androidx.annotation.RequiresApi
import com.hvuitsme.banzashoes.payment.zalopay.api.CreateOrderResponse
import com.hvuitsme.banzashoes.payment.zalopay.api.ZaloPayApi
import com.hvuitsme.banzashoes.payment.zalopay.config.ZaloPayConfig
import com.hvuitsme.banzashoes.payment.zalopay.util.Helpers
import com.hvuitsme.banzashoes.payment.zalopay.util.HMacUtil

class ZaloPayRepository(
    private val api: ZaloPayApi
) {
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun createOrder(amount: Long): String {
        val appId = ZaloPayConfig.ZP_APP_ID.toString()
        val appUser = "Android_BanzaShoes"
        val appTime = System.currentTimeMillis().toString()
        val appTransId = Helpers.getAppTransId()
        val description = "Payment for order $appTransId"
        val rawData = listOf(appId, appTransId, appUser, amount.toString(), appTime, "{}", "[]").joinToString("|")
        val mac = HMacUtil.hMacHexStringEncode(HMacUtil.HMACSHA256, ZaloPayConfig.ZP_MAC_KEY, rawData)
            ?: throw Exception("MAC calculation failed")
        val resp: CreateOrderResponse = api.createOrder(
            appId, appUser, appTime, amount.toString(), appTransId,
            "{}", "[]", "zalopayapp", description, mac
        )
        return if (resp.returnCode == "1" && resp.zpTransToken != null) resp.zpTransToken
        else throw Exception("Failed to create ZaloPay order: ${resp.returnCode}")
    }
}