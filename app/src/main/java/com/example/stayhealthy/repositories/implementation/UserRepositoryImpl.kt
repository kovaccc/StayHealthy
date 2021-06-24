package com.example.stayhealthy.repositories.implementation


import android.content.Context
import android.util.Log
import com.example.stayhealthy.data.models.domain.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.example.stayhealthy.utils.Result
import com.example.stayhealthy.repositories.UserRepository
import java.lang.Exception
import com.google.firebase.auth.AuthResult
import com.example.stayhealthy.common.extensions.await
import com.example.stayhealthy.data.database.StayHealthyDB
import com.example.stayhealthy.data.database.storage.ProfileStorage
import com.example.stayhealthy.utils.FirebaseStorageManager
import com.google.firebase.auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow


private const val TAG = "UserRepositoryImpl"

class UserRepositoryImpl(
        private val firebaseStorageManager: FirebaseStorageManager,
        private val profileStorage: ProfileStorage,
        private val database: StayHealthyDB,
) :
        UserRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override val user: User
        get() = profileStorage.user

    override fun getLocalUserLiveData() = profileStorage.getUserLiveData()

    override suspend fun getLocalUserAsync(): User {
        return profileStorage.getUserAsync()
    }

    override suspend fun logInUserFromAuthWithEmailAndPassword(
            email: String,
            password: String
    ): Result<FirebaseUser?> {
        try {
            return when (val resultDocumentSnapshot =
                    firebaseAuth.signInWithEmailAndPassword(email, password).await()) {
                is Result.Success -> {
                    val firebaseUser = resultDocumentSnapshot.data.user
                    Result.Success(firebaseUser)
                }
                is Result.Error -> {
                    Log.e(TAG, "${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled -> {
                    Log.e(TAG, "${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        } catch (exception: Exception) {
            return Result.Error(exception)
        }
    }


    override suspend fun registerUserFromAuthWithEmailAndPassword(
            email: String,
            password: String,
            context: Context
    ): Result<FirebaseUser?> {
        try {
            return when (val resultDocumentSnapshot =
                    firebaseAuth.createUserWithEmailAndPassword(email, password).await()) {
                is Result.Success -> {
                    val firebaseUser = resultDocumentSnapshot.data.user
                    Result.Success(firebaseUser)
                }
                is Result.Error -> {
                    Log.e(TAG, "${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled -> {
                    Log.e(TAG, "${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        } catch (exception: Exception) {
            return Result.Error(exception)
        }
    }


    override suspend fun signInWithCredential(authCredential: AuthCredential): Result<AuthResult?> {
        return firebaseAuth.signInWithCredential(authCredential).await()
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Void?> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
        } catch (exception: Exception) {
            Result.Error(exception)
        }
    }


    override suspend fun checkUserLoggedIn(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun logOutUser() {
        firebaseAuth.signOut()
        database.clearAllTables()
    }


    override suspend fun createUserInFirestore(user: User): Result<Void?> {
        profileStorage.persistUser(user)
        return firebaseStorageManager.createUserInFirestore(user)
    }

    override suspend fun getUserFromFirestore(userId: String): Result<User> {
        val result = firebaseStorageManager.getUserFromFirestore(userId)
        when (result) {
            is Result.Success -> {
                profileStorage.persistUser(result.data)
            }
            else -> {
            }
        }
        return result
    }


    override suspend fun updateUserInFirestore(user: User): Result<Void?> {
        profileStorage.persistUser(user)
        return firebaseStorageManager.updateUserInFirestore(user)
    }


    @ExperimentalCoroutinesApi
    override suspend fun listenOnUserChanged(userId: String): Flow<Result<User>?> {
        return firebaseStorageManager.listenOnUserChanged(userId)
    }

}