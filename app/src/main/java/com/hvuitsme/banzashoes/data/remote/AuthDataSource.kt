package com.hvuitsme.banzashoes.data.remote

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.hvuitsme.banzashoes.data.model.User
import kotlinx.coroutines.tasks.await

class AuthDataSource {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val database = FirebaseDatabase.getInstance()
    val ref = database.getReference("Users")

    suspend fun signUp(user: User): AuthResult {
        val authResult = auth.createUserWithEmailAndPassword(user.email, user.password).await()
        auth.currentUser?.let {
            ref.child(it.uid).setValue(user.copy(id = it.uid))
        }
        return authResult
    }

    suspend fun signIn(email: String, password: String): AuthResult {
        return auth.signInWithEmailAndPassword(email, password).await()
    }
}