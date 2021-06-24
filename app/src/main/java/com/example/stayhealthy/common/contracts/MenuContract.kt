package com.example.stayhealthy.common.contracts

import android.provider.BaseColumns


object MenuContract {


    internal const val ROOT_NAME = "Menu"
    internal const val FRUITS_NODE_NAME = "Fruits"
    internal const val GRAINS_PASTA_NODE_NAME = "Grains&Pasta"
    internal const val PROTEINS_NODE_NAME = "Proteins"
    internal const val VEGETABLES_NODE_NAME = "Vegetables"

    object Columns {
        const val ID = BaseColumns._ID
        const val MENU_ITEM_ID = "id"
        const val MENU_ITEM_NAME = "Name"
        const val MENU_ITEM_QUANTITY = "Quantity"
        const val MENU_ITEM_CALORIES = "Calories"
        const val MENU_ITEM_IMAGE = "Image"
    }

}