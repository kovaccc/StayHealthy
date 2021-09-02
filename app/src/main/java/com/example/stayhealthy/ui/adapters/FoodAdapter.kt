package com.example.stayhealthy.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import com.example.stayhealthy.R
import com.example.stayhealthy.common.extensions.setImageWithURI
import com.example.stayhealthy.ui.viewHolders.FoodViewHolder
import com.example.stayhealthy.data.models.domain.Food
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import kotlinx.android.synthetic.main.food_item.*


private const val TAG = "FoodAdapter"

class FoodAdapter(options: FirebaseRecyclerOptions<Food>?, private val listener: OnFoodClickListener) : FirebaseRecyclerAdapter<Food, FoodViewHolder>(options!!) {


    interface OnFoodClickListener {
        fun onAddClick(foodItem: Food)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        Log.d(TAG, "onCreateViewHolder: new view requested")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.food_item, parent, false)
        return FoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: FoodViewHolder, position: Int, foodItem: Food) {
        Log.d(TAG, "onBindViewHolder: new view requested")

        holder.food_image.setImageWithURI(foodItem.Image.toUri())

        holder.bind(foodItem, listener)
    }
}