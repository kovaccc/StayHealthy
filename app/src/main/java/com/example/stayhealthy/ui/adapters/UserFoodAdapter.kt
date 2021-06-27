package com.example.stayhealthy.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.stayhealthy.data.models.domain.UserFood
import com.example.stayhealthy.databinding.FoodItemBinding
import com.example.stayhealthy.ui.viewHolders.UserFoodViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions

class UserFoodAdapter(
        options: FirestoreRecyclerOptions<UserFood>?,
        private val listener: OnUserFoodClickListener
) : FirestoreRecyclerAdapter<UserFood, UserFoodViewHolder>(options!!){


    interface OnUserFoodClickListener {
        fun onAddClick(foodItem: UserFood)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserFoodViewHolder {
        val binding = FoodItemBinding
                .inflate(LayoutInflater.from(parent.context), parent, false)
        return UserFoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserFoodViewHolder, position: Int, userFood: UserFood) {
       holder.bind(userFood, listener)
    }

}