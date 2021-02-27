package com.example.stayhealthy.viewHolder.recyclerAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.stayhealthy.R
import com.example.stayhealthy.viewHolder.MealPlanViewHolder
import com.example.stayhealthy.model.MealPlanItem
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions


private const val TAG = "MealPlanAdapter"
class MealPlanAdapter(private var options: FirestoreRecyclerOptions<MealPlanItem>?) : FirestoreRecyclerAdapter<MealPlanItem, MealPlanViewHolder>(options!!){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPlanViewHolder {
        Log.d(TAG, "onCreateViewHolder: new view requested")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.meal_plan_item, parent, false)
        return MealPlanViewHolder(view)
    }

    override fun onBindViewHolder(holder: MealPlanViewHolder, position: Int, mealplanItem: MealPlanItem) {
        Log.d(TAG, "onBindViewHolder: new view requested")

        holder.bind(mealplanItem)
    }

    fun deleteItem(position: Int) {
        Log.d(TAG, "deleteItem: called")
        snapshots.getSnapshot(position).reference.delete()
    }
}