package com.example.stayhealthy.common

import com.example.stayhealthy.util.validation.Input

interface ValidatableForm<RequestType> {
    val request: RequestType
    val mandatoryFields: List<Input<*>>

    val isValid: Boolean
        get() {
            var valid = true
            mandatoryFields.forEach { input ->
                if (!input.validate()) valid = false
            }
            return valid
        }

    fun resetForm() {}
    fun setCurrentStateAsInitial() {}
}