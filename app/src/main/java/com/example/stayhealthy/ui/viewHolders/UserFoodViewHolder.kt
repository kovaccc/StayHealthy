package com.example.stayhealthy.ui.viewHolders

import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.example.stayhealthy.R
import com.example.stayhealthy.common.extensions.setImageWithURI
import com.example.stayhealthy.data.models.domain.UserFood
import com.example.stayhealthy.databinding.FoodItemBinding
import com.example.stayhealthy.ui.adapters.UserFoodAdapter


class UserFoodViewHolder(private val binding: FoodItemBinding) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(userFood: UserFood, listener: UserFoodAdapter.OnUserFoodClickListener) {
        with(binding) {

            foodImage.setImageWithURI(userFood.image.toUri())
            foodQuantity.text = itemView.context.getString(
                R.string.food_quantity_value,
                userFood.quantity.toString()
            )
            foodCalories.text = itemView.context.getString(
                R.string.food_calorie_value,
                userFood.calories.toString()
            )
            foodName.text = userFood.name

            btnAddToOrder.setOnClickListener {
                listener.onAddClick(userFood)
            }
        }
    }
}