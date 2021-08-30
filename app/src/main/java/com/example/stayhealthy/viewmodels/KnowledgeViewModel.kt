package com.example.stayhealthy.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.stayhealthy.data.models.domain.KnowledgeBaseItem
import com.example.stayhealthy.repositories.KnowledgeRepository
import com.firebase.ui.database.FirebaseRecyclerOptions

class KnowledgeViewModel(repository: KnowledgeRepository) : ViewModel() {

    private val _currentKnowledgeBaseOptionsMLD = MutableLiveData<FirebaseRecyclerOptions<KnowledgeBaseItem>>(FirebaseRecyclerOptions.Builder<KnowledgeBaseItem>()
            .setQuery(repository.createKnowledgeBaseQuery(), KnowledgeBaseItem::class.java)
            .build())
    val currentKnowledgeBaseOptionsLD: LiveData<FirebaseRecyclerOptions<KnowledgeBaseItem>>
        get() = _currentKnowledgeBaseOptionsMLD

}