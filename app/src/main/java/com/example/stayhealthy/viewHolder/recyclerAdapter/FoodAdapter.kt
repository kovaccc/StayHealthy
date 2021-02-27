package com.example.stayhealthy.viewHolder.recyclerAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.stayhealthy.R
import com.example.stayhealthy.viewHolder.FoodViewHolder
import com.example.stayhealthy.model.Food
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.food_item.*


private const val TAG = "FoodAdapter"
class FoodAdapter(private var options: FirebaseRecyclerOptions<Food>?, private val listener: OnFoodClickListener) : FirebaseRecyclerAdapter<Food, FoodViewHolder>(options!!) {


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

        Picasso.with(holder.food_image.context).load(foodItem.Image)
                .error(R.drawable.placeholder)
                .placeholder(R.drawable.placeholder)
                .into(holder.food_image)

        holder.bind(foodItem, listener)
    }
}