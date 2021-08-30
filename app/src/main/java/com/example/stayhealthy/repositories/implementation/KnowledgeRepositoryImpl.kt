package com.example.stayhealthy.repositories.implementation

import com.example.stayhealthy.repositories.KnowledgeRepository
import com.example.stayhealthy.util.FirebaseStorageManager
import com.google.firebase.database.Query

class KnowledgeRepositoryImpl(private val firebaseStorageManager: FirebaseStorageManager) : KnowledgeRepository {

    override fun createKnowledgeBaseQuery(): Query {
        return firebaseStorageManager.createKnowledgeBaseQuery()
    }
}