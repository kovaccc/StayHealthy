package com.example.stayhealthy.util.validation

import com.example.stayhealthy.R
import java.util.*
import java.util.regex.Pattern

//return resId of error message, if valid null
@SuppressWarnings("NestedBlockDepth")
fun validate(input: String?, type: InputType?): Int? {
    val errors: ArrayList<Int> = arrayListOf()
    type?.let {
        type.validationTypes.forEach { validation ->
            when (validation) {
                is Required -> {
                    if (input.isNullOrEmpty()) {
                        errors.add(validation.resolveToMessage())
                        return errors.first()
                    }
                }

                is Length -> {
                    if (input!!.length < validation.value) {
                        errors.add(validation.resolveToMessage())
                    }
                }
                is Regex -> {
                    val pattern = Pattern.compile(validation.value)
                    val matcher = pattern.matcher(input!!)
                    if (!matcher.matches()) {
                        errors.add(validation.resolveToMessage())
                    }
                }
            }
        }

    }

    return errors.firstOrNull()
}

enum class InputType {
    QUANTITY, CALORIES, FOOD_NAME;

    val validationTypes: Array<ValidationType>
        get() {
            return when (this) {
                QUANTITY -> arrayOf(Required)
                CALORIES -> arrayOf(Required)
                FOOD_NAME -> arrayOf(Required)
            }
        }
}

sealed class ValidationType {
    fun resolveToMessage(): Int {
        return when (this) {
            is Required -> R.string.validation_error_required
            is Length -> R.string.too_short
            is Regex -> R.string.invalid_pattern
        }
    }
}

object Required : ValidationType()
data class Length(val value: Int) : ValidationType()
data class Regex(val value: String) : ValidationType()

