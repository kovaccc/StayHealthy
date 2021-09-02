package com.example.stayhealthy.repositories

import android.content.Context
import com.example.stayhealthy.data.models.domain.User
import com.example.stayhealthy.data.models.persistance.DBUser
import com.google.firebase.auth.FirebaseUser
import com.example.stayhealthy.util.Result
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import kotlinx.coroutines.flow.Flow


interface UserRepository {

    val user: User

    fun getLocalUserLiveData(): Flow<DBUser?>

    suspend fun getLocalUserAsync(): User

    fun getUserNullable(): User?

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