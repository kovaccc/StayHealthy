package com.example.stayhealthy.repository.implementation


import android.content.Context
import android.util.Log
import com.example.stayhealthy.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.example.stayhealthy.utils.Result
import com.example.stayhealthy.repository.UserRepository
import java.lang.Exception
import com.google.firebase.auth.AuthResult
import com.example.stayhealthy.extension.await
import com.example.stayhealthy.repository.UsersContract
import com.google.firebase.auth.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow


private const val TAG = "UserRepositoryImpl"

class UserRepositoryImpl : UserRepository {
    private val firestoreInstance = FirebaseFirestore.getInstance()
    private val userCollection = firestoreInstance.collection(UsersContract.COLLECTION_NAME)
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()


    override suspend fun logInUserFromAuthWithEmailAndPassword(email: String, password: String): Result<FirebaseUser?>
    {
        try
        {
            return when(val resultDocumentSnapshot = firebaseAuth.signInWithEmailAndPassword(email, password).await())
            {
                is Result.Success -> {
                    Log.i(TAG, "Result.Success")
                    val firebaseUser = resultDocumentSnapshot.data.user
                    Result.Success(firebaseUser)
                }
                is Result.Error -> {
                    Log.e(TAG, "${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled ->  {
                    Log.e(TAG, "${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        }
        catch (exception: Exception)
        {
            return Result.Error(exception)
        }
    }



    override suspend fun registerUserFromAuthWithEmailAndPassword(email: String, password: String, context: Context): Result<FirebaseUser?>
    {
        try
        {
            return when(val resultDocumentSnapshot = firebaseAuth.createUserWithEmailAndPassword(email, password).await())
            {
                is Result.Success -> {
                    Log.i(TAG, "Result.Success")
                    val firebaseUser = resultDocumentSnapshot.data.user
                    Result.Success(firebaseUser)
                }
                is Result.Error -> {
                    Log.e(TAG, "${resultDocumentSnapshot.exception}")
                    Result.Error(resultDocumentSnapshot.exception)
                }
                is Result.Canceled ->  {
                    Log.e(TAG, "${resultDocumentSnapshot.exception}")
                    Result.Canceled(resultDocumentSnapshot.exception)
                }
            }
        }
        catch (exception: Exception)
        {
            return Result.Error(exception)
        }
    }

    override suspend fun checkUserLoggedIn(): FirebaseUser?
    {
        return firebaseAuth.currentUser
    }

    override suspend fun logOutUser()
    {
        firebaseAuth.signOut()
    }



    override suspend fun createUserInFirestore(user: User): Result<Void?>
    {
        return try
        {
            userCollection.document(user.id).set(user).await()
        }
        catch (exception: Exception)
        {
            Result.Error(exception)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Void?>
    {
        return try
        {
            firebaseAuth.sendPasswordResetEmail(email).await()
        }
        catch (exception: Exception)
        {
            Result.Error(exception)
        }
    }

    override suspend fun getUserFromFirestore(userId: String): Result<User>
    {
        try
        {
            return when(val resultDocumentSnapshot = userCollection.document(userId).get().await())
            {
                is Result.Success -> {
                    val user = resultDocumentSnapshot.data.toObject(User::class.java)!!
                    Result.Success(user)
                }
                is Result.Error -> Result.Error(resultDocumentSnapshot.exception)
                is Result.Canceled -> Result.Canceled(resultDocumentSnapshot.exception)
            }
        }
        catch (exception: Exception)
        {
            return Result.Error(exception)
        }
    }


    override suspend fun signInWithCredential(authCredential: AuthCredential): Result<AuthResult?>
    {
        return firebaseAuth.signInWithCredential(authCredential).await()
    }


    override suspend fun updateUserInFirestore(user: User): Result<Void?> {
        return try
        {
            userCollection.document(user.id).set(user).await()
        }
        catch (exception: Exception)
        {
            Result.Error(exception)
        }
    }


    @ExperimentalCoroutinesApi
    override suspend fun listenOnUserChanged(userId: String): Flow<Result<User>?> {

        Log.d(TAG, "listenOnUserChanged starts")
        return callbackFlow { //  -> Kotlin Flows
            val listenerRegistration = userCollection
                    .document(userId)
                    .addSnapshotListener { snapshot, e ->
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e)
                            offer(Result.Error(e))
                        }
                        if (snapshot != null && snapshot.exists()) {
                            val user = snapshot.toObject(User::class.java)!!
                            Log.d(TAG, "listenOnUserChanged: listen success with $user")
                            offer(Result.Success(user)) // offer values to the channel that will be collected in ViewModel
                        } else {
                            Log.d(TAG, "Current data: null")
                        }
                    }
            //Finally if collect is not in use or collecting any data cancel this channel to prevent any leak and remove the subscription listener to the database
            awaitClose {
                Log.d(TAG, "Cancelling user listener")
                listenerRegistration.remove()
            }
            Log.d(TAG, "listenOnUserChanged ends")
        }
    }

}