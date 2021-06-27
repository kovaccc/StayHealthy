package com.example.stayhealthy.ui.forms

import androidx.lifecycle.MediatorLiveData
import com.example.stayhealthy.common.ValidatableForm
import com.example.stayhealthy.data.models.requests.AddFoodRequest
import com.example.stayhealthy.util.validation.InputText
import com.example.stayhealthy.util.validation.InputType

class AddFoodForm: ValidatableForm<AddFoodRequest> {

    val quantity = InputText(InputType.QUANTITY)
    val calories = InputText(InputType.CALORIES)
    val name = InputText(InputType.FOOD_NAME)

    override val request get() = AddFoodRequest (
            quantity = quantity.unwrappedValue.toInt(),
            calories = calories.unwrappedValue.toInt(),
            name = name.unwrappedValue

    )

    override val mandatoryFields: List<InputText>
        get() = listOf(name, calories, quantity)

    val liveDataManager = MediatorLiveData<Int?>().apply {
        addSource(name.error) { value -> setValue(value) }
        addSource(calories.error) { value -> setValue(value) }
        addSource(quantity.error) { value -> setValue(value) }
    }
}