package com.example.stayhealthy.util.validation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

interface Input<T> {

    //Expose input to observers
    val input: MutableLiveData<T>

    var value: T?

    val unwrappedValue: T
        get() = value!!

    val error: LiveData<Int?>

    val errorValue: Int?

    fun validate(): Boolean

}