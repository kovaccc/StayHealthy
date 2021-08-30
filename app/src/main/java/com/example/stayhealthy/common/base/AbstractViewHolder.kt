package com.example.stayhealthy.common.base

import android.view.View
import androidx.recyclerview.widget.RecyclerView


typealias ItemClickListener<T> = ((T, Int) -> Unit)?

abstract class AbstractViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(model: T, position: Int, listener: ItemClickListener<T> = null)
}
