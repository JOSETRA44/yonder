package com.josetra.yonder.data.repository

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.josetra.yonder.data.User
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    suspend fun loginWithEmail(email: String, pass: String) = auth.signInWithEmailAndPassword(email, pass).await()

    suspend fun loginWithGoogle(credential: AuthCredential) = auth.signInWithCredential(credential).await()

    suspend fun registerUser(email: String, pass: String) = auth.createUserWithEmailAndPassword(email, pass).await()

    suspend fun saveUserToDatabase(userId: String, user: User) {
        database.getReference("users").child(userId).setValue(user).await()
    }
}
