package com.example.stayhealthy.viewHolder

import android.util.Log
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView



private const val TAG = "RecyclerItemTouch"

class RecyclerItemTouchHelper(recyclerView: RecyclerView, private val listener: OnRecyclerTouchListener) : RecyclerView.SimpleOnItemTouchListener()   {

    interface OnRecyclerTouchListener {
        fun onSwipe(viewHolder: RecyclerView.ViewHolder)
    }

   private val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            Log.d(TAG, "onSwiped: called")
            listener.onSwipe(viewHolder)
        }
    }).attachToRecyclerView(recyclerView)


}