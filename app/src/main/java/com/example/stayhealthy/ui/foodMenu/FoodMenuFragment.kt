package com.example.stayhealthy.ui.foodMenu

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.stayhealthy.model.Food
import com.example.stayhealthy.R
import com.example.stayhealthy.viewHolder.recyclerAdapter.FoodAdapter
import kotlinx.android.synthetic.main.fragment_food_menu.*
import kotlinx.android.synthetic.main.fragment_food_menu.view.*
import org.koin.android.viewmodel.ext.android.viewModel


private const val TAG = "FoodMenuFragment"
private const val ARG_FOOD_CATEGORY = "category"

class FoodMenuFragment : Fragment(), FoodAdapter.OnFoodClickListener {

    interface OnFoodAdd {
        fun onFoodAdd(foodItem: Food, category: String)
    }

    private var mAdapter: FoodAdapter? = null

    private var isFirstInit: Boolean = true


    var date: String? = null

    var category: String? = null

    private val foodMenuViewModel: FoodMenuViewModel by viewModel()


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView starts")
        val root = inflater.inflate(R.layout.fragment_food_menu, container, false)

            root.searchFood.isFocusable = false

            root.searchFood.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }
                override fun onQueryTextChange(newText: String): Boolean {
                    search(newText)
                    return true
                }
            })

        Log.d(TAG, "onCreateView ends")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated starts")
        super.onViewCreated(view, savedInstanceState)
        recycler_food.layoutManager = LinearLayoutManager(context)
        if(category != null) {
            searchFood.queryHint = getString(R.string.search_food_hint, category)
        }
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


        category = arguments?.getString(ARG_FOOD_CATEGORY)
        Log.d(TAG, "onCreate: arguments are category: $category")

        foodMenuViewModel.getFoodOptionsFrom(category!!)

        foodMenuViewModel.currentFoodLD.observe(this,{options ->
            Log.d(TAG, "onCreate: observing options with value $options, and first init $isFirstInit")
            if(isFirstInit) {
                mAdapter = FoodAdapter(options, this)
                recycler_food.adapter = mAdapter
                isFirstInit = false
                mAdapter!!.startListening()

            } else {
                mAdapter?.updateOptions(options)
                recycler_food.adapter = mAdapter
            }
        })

        Log.d(TAG, "onCreate: ends")
    }

    private fun search(string: String) {

        if(string.isNotEmpty()) {
            val stringAllUpper =  capitalizeAllFirst(string)
            foodMenuViewModel.getFoodOptionsSearchCondition(category!!, stringAllUpper)
        } else {
            foodMenuViewModel.getFoodOptionsFrom(category!!)
        }

    }


    private fun capitalizeAllFirst(value: String): String { // to avoid case sensitive
        val array = value.toCharArray()

        // Uppercase first letter.
        array[0] = Character.toUpperCase(array[0])

        // Uppercase all letters that follow a whitespace character.
        for (i in 1 until array.size) {
            if (Character.isWhitespace(array[i - 1])) {
                array[i] = Character.toUpperCase(array[i])
            } else {
                array[i] = Character.toLowerCase(array[i])
            }
        }
        return String(array)
    }

    override fun onAddClick(foodItem: Food) { // open dialog in activity better then here
        (activity as OnFoodAdd?)?.onFoodAdd(foodItem, category!!)
    }

    override fun onStart() {
        super.onStart()
        if(mAdapter != null) {
            mAdapter!!.startListening()
        }
    }

    override fun onStop() {
        super.onStop()
        if(mAdapter != null) {
            mAdapter!!.stopListening()
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         */
        @JvmStatic
        fun newInstance(category: String) = // you are putting category from food activity through this function
                FoodMenuFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_FOOD_CATEGORY, category)
                    }
                }
    }

}