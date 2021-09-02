package com.example.stayhealthy.common.contracts

import android.provider.BaseColumns

object MealPlanContract {

    internal const val COLLECTION_NAME = "meal plan"

    object Columns {
        const val ID = BaseColumns._ID
        const val MEAL_ITEM_ID = "id"
        const val MEAL_ITEM_NAME = "name"
        const val MEAL_ITEM_QUANTITY = "quantity"
        const val MEAL_ITEM_CALORIES = "calories"
        const val MEAL_ITEM_CATEGORY = "category"
        const val MEAL_ITEM_DATE = "date"
    }

}