package com.example.stayhealthy.ui.foodPlanner

import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stayhealthy.R
import com.example.stayhealthy.viewHolder.recyclerAdapter.MealPlanAdapter
import com.example.stayhealthy.viewHolder.RecyclerItemTouchHelper
import kotlinx.android.synthetic.main.fragment_food_planner.*
import org.koin.android.viewmodel.ext.android.viewModel



private const val TAG = "FoodPlannerFragment"
class FoodPlannerFragment : Fragment(), RecyclerItemTouchHelper.OnRecyclerTouchListener {

    private var mAdapter: MealPlanAdapter? = null

    private val foodPlannerViewModel : FoodPlannerViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView starts")
        val root = inflater.inflate(R.layout.fragment_food_planner, container, false)
        Log.d(TAG, "onCreateView ends")
        return root
    }

    override fun onSwipe(viewHolder: RecyclerView.ViewHolder) {
        Log.d(TAG, "onSwipe called ${viewHolder.adapterPosition}")
        mAdapter?.deleteItem(viewHolder.adapterPosition)
        foodPlannerViewModel.updateCalories()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d(TAG, "onViewCreated starts")

        recycler_meal_plan.layoutManager = LinearLayoutManager(context)
        recycler_meal_plan.addOnItemTouchListener(RecyclerItemTouchHelper(recycler_meal_plan, this))

        Log.d(TAG, "onViewCreated ends")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)

        foodPlannerViewModel.currentMealPlanLD.observe(this, { options ->
            Log.d(TAG, "onCreate: observing firestore adapter options with $options")

            mAdapter = MealPlanAdapter(options)
            recycler_meal_plan.adapter = mAdapter
            mAdapter!!.startListening()

        })

        foodPlannerViewModel.selectedDateLD.observe(this, { date ->
            val dateFormat = DateFormat.getDateFormat(context) // it formats date depending on different parts of a world
            val userDate = dateFormat.format(date)
            etDayPick.setText(userDate)
        })

        foodPlannerViewModel.fruitsLD.observe(this, { fruits ->
            tvFruitsPercentage.text = getString(R.string.food_calorie_value, fruits)

        })

        foodPlannerViewModel.vegetableLD.observe(this, { vegetables ->
            tvVegetablesPercentage.text = getString(R.string.food_calorie_value, vegetables)

        })

        foodPlannerViewModel.proteinsLD.observe(this, { proteins ->
            tvProteinsPercentage.text = getString(R.string.food_calorie_value, proteins)

        })

        foodPlannerViewModel.grainsPastaLD.observe(this, { grainsPasta ->
            tvGrainsPercentage.text = getString(R.string.food_calorie_value, grainsPasta)

        })

        foodPlannerViewModel.mealCaloriesLD.observe(this, { totalCalorie ->
            tvCalories.text = getString(R.string.food_calorie_value, totalCalorie)
        })

        foodPlannerViewModel.toast.observe(this, { message ->
            message?.let {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                foodPlannerViewModel.onToastShown()
            }
        })

        Log.d(TAG, "onCreate ends")
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

}