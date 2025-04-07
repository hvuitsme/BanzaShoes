package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.User
import com.hvuitsme.banzashoes.data.remote.AuthDataSource

class AuthRepoImpl(
    private val authDataSource: AuthDataSource,
) : AuthRepo {
    override suspend fun signUp(user: User): Pair<Boolean, String?> {
        return try {
            authDataSource.signUp(user)
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, e.message)
        }
    }

    override suspend fun signIn(identifier: String, password: String): Pair<Boolean, String?> {
        return try {
            val email = authDataSource.getEmailByIdentifier(identifier)
            if (email == null) {
                Pair(false, "Account not found")
            } else {
                authDataSource.signIn(email, password)
                Pair(true, null)
            }
        } catch (e: Exception) {
            Pair(false, e.message)
        }
    }
}