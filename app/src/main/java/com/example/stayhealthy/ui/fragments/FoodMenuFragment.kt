package com.example.stayhealthy.ui.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stayhealthy.data.models.domain.Food
import com.example.stayhealthy.R
import com.example.stayhealthy.ui.adapters.FoodAdapter
import com.example.stayhealthy.viewmodels.FoodMenuViewModel
import kotlinx.android.synthetic.main.fragment_food_menu.*
import org.koin.androidx.viewmodel.ext.android.sharedViewModel


private const val TAG = "FoodMenuFragment"
private const val ARG_FOOD_CATEGORY = "foodCategory"

class FoodMenuFragment : Fragment(), FoodAdapter.OnFoodClickListener {

    interface OnFoodAdd {
        fun onFoodAdd(foodItem: Food, category: String)
    }

    private var mAdapter: FoodAdapter? = null

    private val foodMenuViewModel by sharedViewModel<FoodMenuViewModel>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView starts")
        val root = inflater.inflate(R.layout.fragment_food_menu, container, false)
        Log.d(TAG, "onCreateView ends")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated starts")
        super.onViewCreated(view, savedInstanceState)
        recycler_food.layoutManager = LinearLayoutManager(context)
        Log.d(TAG, "onViewCreated ends")
    }


    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach: called")
        super.onAttach(context)

        if (context !is OnFoodAdd) {
            throw RuntimeException("$context must implement OnFoodAdd")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)

        foodMenuViewModel.currentFoodOptionsLD.observe(this, { options ->
            mAdapter = FoodAdapter(options, this)
            recycler_food.adapter = mAdapter
            mAdapter?.startListening()
        })

        foodMenuViewModel.isUserFoodLD.observe(this, {
            foodMenuViewModel.getFoodOptionsCategory()
        })

        foodMenuViewModel.tabSelectedFoodCategoryLD.observe(this, { category ->
            foodMenuViewModel.getFoodOptionsCategory()
        })

        Log.d(TAG, "onCreate: ends")
    }


    override fun onAddClick(foodItem: Food) { // open dialog in activity better then here
        foodMenuViewModel.tabSelectedFoodCategoryMLD.value?.let { (activity as OnFoodAdd?)?.onFoodAdd(foodItem, it) }
    }

    override fun onStart() {
        super.onStart()
        mAdapter?.startListening()
    }

    override fun onStop() {
        super.onStop()
        mAdapter?.stopListening()

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        @JvmStatic
        fun newInstance(category: String) =
                FoodMenuFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_FOOD_CATEGORY, category)
                    }
                }
    }

}