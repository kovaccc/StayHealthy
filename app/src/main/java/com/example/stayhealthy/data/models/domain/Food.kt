package com.example.stayhealthy.data.models.domain

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class Food(
        var Name: String = "",
        var Image: String = "",
        var Quantity: Long = 0,
        var Calories: Long = 0
) :
        Parcelable {

    override fun toString(): String { // used for logs
        return "${this.Name}, ${this.Image}, ${this.Quantity}, ${this.Calories}"
    }
}