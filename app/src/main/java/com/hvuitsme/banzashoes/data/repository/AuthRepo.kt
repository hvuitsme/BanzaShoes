package com.hvuitsme.banzashoes.data.repository

import com.hvuitsme.banzashoes.data.model.User

interface AuthRepo {
    suspend fun signUp(user: User): Pair<Boolean, String?>
    suspend fun signIn(email: String, password: String): Pair<Boolean, String?>
}