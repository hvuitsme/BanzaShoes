package com.hvuitsme.banzashoes.data.model

data class Address(
    var id: String = "",
    val userId: String = "",
    val name: String = "",
    val phone: String = "",
    val address: String = "",
    val dfAddress: Boolean = false
)