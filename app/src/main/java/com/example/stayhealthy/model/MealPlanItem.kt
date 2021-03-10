package com.example.stayhealthy.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MealPlanItem (var id: String = "", var name: String = "", var quantity: Long = 0, var calories: Long = 0, var category: String = "", var date: Long = 0)
    : Parcelable {


    override fun toString(): String {
        return "${this.id}, ${this.name}, ${this.quantity}, ${this.category}, ${this.calories}, ${this.date}"
    }

}