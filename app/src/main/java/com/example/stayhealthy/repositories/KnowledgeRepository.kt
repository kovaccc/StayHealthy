package com.example.stayhealthy.repositories

import com.google.firebase.database.Query

interface KnowledgeRepository {

    fun createKnowledgeBaseQuery(): Query
}