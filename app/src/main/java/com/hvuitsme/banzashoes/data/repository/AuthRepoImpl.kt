package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.User
import com.hvuitsme.banzashoes.data.remote.AuthDataSource
import kotlinx.coroutines.tasks.await

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

    override suspend fun signIn(email: String, password: String): Pair<Boolean, String?> {
        return try {
            authDataSource.signIn(email, password)
            Pair(true, null)
        } catch (e: Exception) {
            Pair(false, e.message)
        }
    }
}