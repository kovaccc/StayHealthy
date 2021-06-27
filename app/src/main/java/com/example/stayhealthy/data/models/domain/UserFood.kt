package com.example.stayhealthy.data.models.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class UserFood(
    var id: String = "",
    var name: String = "",
    var image: String = "",
    var quantity: Int = 0,
    var calories: Int = 0,
    var category: String = "",
) :
    Parcelable {

    override fun toString(): String { // used for logs
        return "${this.name}, ${this.image}, ${this.quantity}, ${this.calories}"
    }

    fun asFood(): Food {
        return Food(
            Name = name,
            Quantity = quantity.toLong(),
            Image = image,
            Calories = calories.toLong()
        )
    }
}