package com.example.stayhealthy.repositories

import android.content.Context
import com.bumptech.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ResourceRepository(val context: Context) {

    fun getString(resId: Int, vararg formatValues: Any) = context.resources.getString(resId, *formatValues)

    suspend fun deleteImageCache() = withContext(Dispatchers.IO) {
        Glide.get(context).clearDiskCache()
    }

}