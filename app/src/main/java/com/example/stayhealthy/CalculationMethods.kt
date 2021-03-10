package com.example.stayhealthy

import com.example.stayhealthy.model.User
import com.example.stayhealthy.repository.MenuContract
import kotlin.math.pow


private const val VEGETABLE_CALORIE_NEEDS = 0.4
private const val FRUITS_CALORIE_NEEDS = 0.1
private const val PROTEINS_CALORIE_NEEDS = 0.2
private const val GRAINS_PASTA_CALORIE_NEEDS = 0.3


class CalculationMethods(val user: User) {


    fun calculateBMI(): Double {
        val mBMI: Double
        val heightInMeters = user.height.toDouble() / 100
        mBMI = user.weight / heightInMeters.pow(2.0)
        return mBMI
    }

    private fun calculateCaloriesForStagnation(): Int {
        val mBMR: Double
        val result: Int
        if (user.gender == "Female") {
            mBMR = 655.1 + 9.563 * user.weight + 1.850 * user.height - 4.676 * user.age
            result = (mBMR * parameterActivityLevel()).toInt()
        } else {
            mBMR = 66.47 + 13.75 * user.weight + 5.003 * user.height - 6.755 * user.age
            result = (mBMR * parameterActivityLevel()).toInt()
        }
        return result
    }

    private fun calculateCaloriesFOrWeightGain(): Int {
        return calculateCaloriesForStagnation() + 500
    }

    private fun calculateCaloriesFOrWeightLoss(): Int {
        return calculateCaloriesForStagnation() - 500
    }

    private fun parameterActivityLevel(): Double {
        var coefficient = 0.0
        when (user.activityLevel) {
            "Sedentary" -> coefficient = 1.2
            "Lightly Active" -> coefficient = 1.375
            "Moderately Active" -> coefficient = 1.55
            "Very active" -> coefficient = 1.725
            "Extra active" -> coefficient = 1.9
        }
        return coefficient
    }

    fun calculateCaloriesForOptimizingBMI(): Int {
        return if (calculateBMI() < 18.5) {
            calculateCaloriesFOrWeightGain()
        } else if (calculateBMI() > 18.5 && calculateBMI() < 24.9) {
            calculateCaloriesForStagnation()
        } else {
            calculateCaloriesFOrWeightLoss()
        }
    }


    fun calculateFoodCalorieNeeds(category: String) : Int {
        return when(category) {
            MenuContract.FRUITS_NODE_NAME -> (this.calculateCaloriesForOptimizingBMI() * FRUITS_CALORIE_NEEDS).toInt()
            MenuContract.VEGETABLES_NODE_NAME -> (this.calculateCaloriesForOptimizingBMI() * VEGETABLE_CALORIE_NEEDS).toInt()
            MenuContract.GRAINS_PASTA_NODE_NAME -> (this.calculateCaloriesForOptimizingBMI() * GRAINS_PASTA_CALORIE_NEEDS).toInt()
            MenuContract.PROTEINS_NODE_NAME -> (this.calculateCaloriesForOptimizingBMI() * PROTEINS_CALORIE_NEEDS).toInt()
            else -> 0
        }
    }
}