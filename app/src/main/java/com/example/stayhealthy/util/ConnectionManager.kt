package com.example.stayhealthy.util

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress
import java.net.UnknownHostException

object ConnectionManager {
    suspend fun isInternetAvailable(): Boolean = withContext(Dispatchers.IO) {
        try {
            InetAddress.getByName("www.google.com")
            return@withContext true
        } catch (e: UnknownHostException) {
            // NOOP
        } catch (e: Exception) {
            // NOOP
        }
        return@withContext false
    }
}