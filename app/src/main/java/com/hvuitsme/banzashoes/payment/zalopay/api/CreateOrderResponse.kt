package com.hvuitsme.banzashoes.payment.zalopay.api

import com.google.gson.annotations.SerializedName

data class CreateOrderResponse (
    @SerializedName("return_code") val returnCode: String,
    @SerializedName("return_message") val returnMessage: String?,
    @SerializedName("zp_trans_token") val zpTransToken: String?
)