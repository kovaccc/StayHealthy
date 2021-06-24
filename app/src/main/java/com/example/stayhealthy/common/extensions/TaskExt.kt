package com.example.stayhealthy.common.extensions

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.suspendCancellableCoroutine
import com.example.stayhealthy.utils.Result
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.resume

// Task Extension
// extension of the fireabse's Task object
suspend fun <T> Task<T>.await(): Result<T> // . The Task is an API that represents asynchronous method calls. Firebase authentication and Firestore operations return the result wrapped by Task.
{
    if (isComplete) {
        val e = exception
        return if (e == null) {
            if (isCanceled) {
                Result.Canceled(CancellationException("Task $this was cancelled normally."))
            } else {
                @Suppress("UNCHECKED_CAST")
                Result.Success(result as T)
            }
        } else {
            Result.Error(e)
        }
    }

    return suspendCancellableCoroutine { cont ->
        addOnCompleteListener {
            val e = exception
            if (e == null) {
                @Suppress("UNCHECKED_CAST")
                if (isCanceled) {
                    cont.cancel()
                } else {
                    cont.resume(Result.Success(result as T))
                }
            } else {
                cont.resumeWithException(e)
            }
        }
    }
}