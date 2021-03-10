package com.example.stayhealthy.repository

import android.content.Context
import com.example.stayhealthy.model.User
import com.google.firebase.auth.FirebaseUser
import com.example.stayhealthy.utils.Result
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow


interface UserRepository {

    suspend fun logInUserFromAuthWithEmailAndPassword(
            email: String,
            password: String
    ): Result<FirebaseUser?>

    suspend fun getUserFromFirestore(userId: String): Result<User>?

    suspend fun registerUserFromAuthWithEmailAndPassword(email: String, password: String, context: Context): Result<FirebaseUser?>
    suspend fun createUserInFirestore(user: User): Result<Void?>

    suspend fun sendPasswordResetEmail(
            email: String
    ): Result<Void?>

    suspend fun checkUserLoggedIn(): FirebaseUser?
    suspend fun logOutUser()


    suspend fun signInWithCredential(
            authCredential: AuthCredential
    ): Result<AuthResult?>

    suspend fun updateUserInFirestore(user: User): Result<Void?>

    suspend fun listenOnUserChanged(userId: String): Flow<Result<User>?>

}