package com.example.stayhealthy.viewHolder

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.stayhealthy.R
import com.example.stayhealthy.viewHolder.recyclerAdapter.FoodAdapter
import com.example.stayhealthy.model.Food
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.food_item.*

class FoodViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer{

    fun bind(food: Food, listener: FoodAdapter.OnFoodClickListener) {

            food_quantity.text = this.itemView.context.getString(R.string.food_quantity_value, food.Quantity.toString())
            food_calories.text = this.itemView.context.getString(R.string.food_calorie_value, food.Calories.toString())
            food_name.text = food.Name

        btnAddToOrder.setOnClickListener {
            listener.onAddClick(food)
        }
    }
}