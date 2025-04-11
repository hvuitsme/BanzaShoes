package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Address

interface AddressRepo {
    suspend fun addAddress(address: Address): Boolean
    suspend fun getAddress(userId: String): List<Address>
    suspend fun updateAddress(address: Address): Boolean
    suspend fun deleteAddress(address: Address): Boolean
}