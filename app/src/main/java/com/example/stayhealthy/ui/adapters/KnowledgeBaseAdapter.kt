package com.example.stayhealthy.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import com.example.stayhealthy.common.base.AbstractViewHolder
import com.example.stayhealthy.common.base.ItemClickListener
import com.example.stayhealthy.common.extensions.setImageWithURI
import com.example.stayhealthy.data.models.domain.KnowledgeBaseItem
import com.example.stayhealthy.databinding.KnowledgeBaseItemBinding
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions

class KnowledgeBaseAdapter(options: FirebaseRecyclerOptions<KnowledgeBaseItem>?, private val itemClickListener: ((KnowledgeBaseItem, Int) -> Unit)? = null)
    : FirebaseRecyclerAdapter<KnowledgeBaseItem, KnowledgeBaseViewHolder>(options!!) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): KnowledgeBaseViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = KnowledgeBaseItemBinding.inflate(layoutInflater, parent, false)
        return KnowledgeBaseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: KnowledgeBaseViewHolder, position: Int, model: KnowledgeBaseItem) {
        holder.bind(model, position, itemClickListener)
    }

}


class KnowledgeBaseViewHolder(view: View) : AbstractViewHolder<KnowledgeBaseItem>(view) {

    lateinit var binding: KnowledgeBaseItemBinding

    constructor(binding: KnowledgeBaseItemBinding) : this(binding.root) {
        this.binding = binding
    }


    override fun bind(model: KnowledgeBaseItem, position: Int, listener: ItemClickListener<KnowledgeBaseItem>) {

        with(binding) {
            knowledgeBaseItem = model
            executePendingBindings()

            ivKnowledgeItemImage.setImageWithURI(model.Image.toUri())

            itemView.setOnClickListener {
                listener?.invoke(model, position)
            }

        }

    }
}
