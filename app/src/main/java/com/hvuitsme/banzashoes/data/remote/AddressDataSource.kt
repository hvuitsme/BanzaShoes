package com.hvuitsme.banzashoes.data.remote

import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.banzashoes.data.model.Address
import kotlinx.coroutines.tasks.await

class AddressDataSource {
    private val database = FirebaseDatabase.getInstance()
    private val ref = database.getReference("Address")

    suspend fun addAddress(address: Address): Boolean {
        return try {
            val key = ref.push().key ?: return false
            address.id = key
            ref.child(key).setValue(address).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun getAddress(userId: String): List<Address> {
        return try {
            val snapshot = ref.orderByChild("userId").equalTo(userId).get().await()
            val addresses = mutableListOf<Address>()
            for (child in snapshot.children) {
                val address = child.getValue(Address::class.java)
                address?.let { addresses.add(it) }
            }
            addresses
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun updateAddress(address: Address): Boolean {
        return try {
            if(address.id.isNotEmpty()){
                ref.child(address.id).setValue(address).await()
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun deleteAddress(address: Address): Boolean {
        return try {
            if(address.id.isNotEmpty()){
                ref.child(address.id).removeValue().await()
                true
            } else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}