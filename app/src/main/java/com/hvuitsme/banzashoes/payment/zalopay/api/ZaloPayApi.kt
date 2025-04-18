package com.hvuitsme.banzashoes.payment.zalopay.api

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ZaloPayApi {
    @FormUrlEncoded
    @POST("v2/create")
    suspend fun createOrder(
        @Field("app_id") appId: String,
        @Field("app_user") appUser: String,
        @Field("app_time") appTime: String,
        @Field("amount") amount: String,
        @Field("app_trans_id") appTransId: String,
        @Field("embed_data") embedData: String = "{}",
        @Field("item") items: String = "[]",
        @Field("bank_code") bankCode: String = "zalopayapp",
        @Field("description") description: String,
        @Field("mac") mac: String
    ): CreateOrderResponse
}