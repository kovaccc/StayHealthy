package com.example.stayhealthy.util

/**
 * A call whose result doesn't matter, try it and ignore exception
 */
@Suppress("TooGenericExceptionCaught")
inline fun failableCall(call: () -> Unit) {
    try {
        call()
    } catch (e: Throwable) {}
}
