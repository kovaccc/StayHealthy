package com.example.stayhealthy.viewHolder

import android.content.Context
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView



private const val TAG = "RecyclerItemTouch"

class RecyclerItemTouchHelper(context: Context, recyclerView: RecyclerView, private val listener: OnRecyclerTouchListener) : RecyclerView.SimpleOnItemTouchListener()   {

    interface OnRecyclerTouchListener {
        fun onSwipe(viewHolder: RecyclerView.ViewHolder)
        fun onItemDoubleClick(view: View, position: Int)

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


    // add the gestureDetector
    private val gestureDetector = GestureDetectorCompat(context, object : GestureDetector.SimpleOnGestureListener() {


        override fun onDoubleTap(e: MotionEvent): Boolean {
            Log.d(TAG, ".onDoubleTap: starts")
            val childView = recyclerView.findChildViewUnder(e.x, e.y) // if you don't click on recycler item childView will have null value

            if(childView != null) {
                Log.d(TAG, ".onDoubleTap calling listener.onItemDoubleClick")
                listener.onItemDoubleClick(childView, recyclerView.getChildAdapterPosition(childView))
            }
            Log.d(TAG, ".onDoubleTap ends")
            return true
        }

    })

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean { // this method see all MotionEvents on this view
        Log.d(TAG, ".onInterceptTouchEvent: starts $e")
        val result = gestureDetector.onTouchEvent(e)
        Log.d(TAG, ".onInterceptTouchEvent() returning: $result")
//        return super.onInterceptTouchEvent(rv, e)
        return result
    }



}