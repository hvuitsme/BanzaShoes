package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.Address
import com.hvuitsme.banzashoes.data.remote.AddressDataSource

class AddressRepoImpl(private val addressDataSource: AddressDataSource) : AddressRepo {
    override suspend fun addAddress(address: Address): Boolean {
        return addressDataSource.addAddress(address)
    }
    override suspend fun getAddress(userId: String): List<Address> {
        return addressDataSource.getAddress(userId)
    }
    override suspend fun updateAddress(address: Address): Boolean {
        return addressDataSource.updateAddress(address)
    }
    override suspend fun deleteAddress(address: Address): Boolean {
        return addressDataSource.deleteAddress(address)
    }
}