package com.example.stayhealthy.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.stayhealthy.R
import com.example.stayhealthy.model.MealPlanItem
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.meal_plan_item.*

class MealPlanViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {

    fun bind(mealPlanItem: MealPlanItem) {
        tvMealPlanItemName.text = mealPlanItem.name
        tvMealPlanItemCalories.text = this.itemView.context.getString(R.string.food_calorie_value,mealPlanItem.calories.toString())
        tvMealPlanItemCategory.text = mealPlanItem.category
        tvMealPlanItemQuantity.text = this.itemView.context.getString(R.string.food_quantity_value, mealPlanItem.quantity.toString())

    }
}