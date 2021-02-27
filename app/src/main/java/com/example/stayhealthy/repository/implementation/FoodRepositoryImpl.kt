package com.example.stayhealthy.repository.implementation

import com.example.stayhealthy.repository.FoodRepository
import com.example.stayhealthy.repository.MenuContract
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query


class FoodRepositoryImpl: FoodRepository {

    private val firebaseInstance = FirebaseDatabase.getInstance()

    override fun createFoodQuery(category: String): Query {
        return firebaseInstance.getReference(MenuContract.ROOT_NAME).child(category)
    }

    override fun createFoodQuerySearchCondition(category: String, searchCondition: String): Query {
        return firebaseInstance.getReference(MenuContract.ROOT_NAME).child(category).orderByChild(MenuContract.Columns.MENU_ITEM_NAME).startAt(searchCondition).endAt(searchCondition + "\uf8ff")
    }
}