package com.example.stayhealthy.viewHolder.recyclerAdapter

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chauthai.swipereveallayout.ViewBinderHelper
import com.example.stayhealthy.R
import com.example.stayhealthy.model.MealPlanItem
import com.example.stayhealthy.viewHolder.MealPlanViewHolder
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import kotlinx.android.synthetic.main.meal_plan_item.*


private const val TAG = "MealPlanAdapter"
class MealPlanAdapter(
    private var options: FirestoreRecyclerOptions<MealPlanItem>?,
    private val listener: OnMealPlanItemClickListener
) : FirestoreRecyclerAdapter<MealPlanItem, MealPlanViewHolder>(options!!){


    interface OnMealPlanItemClickListener {
        fun onEditClick(mealPlanItem: MealPlanItem, viewHolder: RecyclerView.ViewHolder)
        fun onDeleteClick(viewHolder: RecyclerView.ViewHolder)
    }

    private val viewBinderHelper = ViewBinderHelper()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MealPlanViewHolder {
        Log.d(TAG, "onCreateViewHolder: new view requested")
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.meal_plan_item,
            parent,
            false
        )
        return MealPlanViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MealPlanViewHolder,
        position: Int,
        mealplanItem: MealPlanItem
    ) {
        Log.d(TAG, "onBindViewHolder: new view requested")


        //swipe functionality
        viewBinderHelper.apply {
            setOpenOnlyOne(true)
            bind(holder.swipe_layout, mealplanItem.toString()) // string that identifies viewHolder item
            closeLayout(mealplanItem.toString())
        }


        holder.bind(mealplanItem, listener)
    }

    fun deleteItem(position: Int) {
        Log.d(TAG, "deleteItem: called")
        snapshots.getSnapshot(position).reference.delete()
    }

    fun updateItem(mealplanItem: MealPlanItem, position: Int) {
        Log.d(TAG, "deleteItem: called")
        snapshots.getSnapshot(position).reference.set(mealplanItem)
    }

    override fun getItem(position: Int): MealPlanItem {
        return super.getItem(position)
    }

    // to restore/save the open/close state when the device's orientation is changed
    fun saveStates(outState: Bundle) {
        viewBinderHelper.saveStates(outState)
    }

    fun restoreStates(inState: Bundle) {
        viewBinderHelper.restoreStates(inState)
    }
}