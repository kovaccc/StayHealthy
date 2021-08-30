package com.example.stayhealthy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.stayhealthy.R
import com.example.stayhealthy.databinding.FragmentKnowledgeBaseBinding
import com.example.stayhealthy.ui.adapters.KnowledgeBaseAdapter
import com.example.stayhealthy.viewmodels.KnowledgeViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class KnowledgeBaseFragment : Fragment() {

    private val knowledgeViewModel: KnowledgeViewModel by viewModel()
    private lateinit var binding: FragmentKnowledgeBaseBinding
    private var knowledgeAdapter: KnowledgeBaseAdapter? = null

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate<FragmentKnowledgeBaseBinding>(
                inflater,
                R.layout.fragment_knowledge_base,
                container,
                false
        ).apply {
            lifecycleOwner = this@KnowledgeBaseFragment.viewLifecycleOwner
            viewModel = this@KnowledgeBaseFragment.knowledgeViewModel
        }
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        knowledgeViewModel.currentKnowledgeBaseOptionsLD.observe(this, { options ->
            knowledgeAdapter = KnowledgeBaseAdapter(options, itemClickListener = { model, position ->
                Toast.makeText(context, "$model, $position", Toast.LENGTH_SHORT).show()
            })
            binding.rvKnowledgeList.adapter = knowledgeAdapter
            knowledgeAdapter?.startListening()
        })
    }

    override fun onStart() {
        super.onStart()
        knowledgeAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        knowledgeAdapter?.stopListening()

    }

}
