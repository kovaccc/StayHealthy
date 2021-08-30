package com.example.stayhealthy.ui.fragments

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
import com.example.stayhealthy.ui.dialogs.*
import com.example.stayhealthy.data.models.domain.MealPlanItem
import com.example.stayhealthy.ui.adapters.MealPlanAdapter
import com.example.stayhealthy.viewmodels.FoodPlannerViewModel
import kotlinx.android.synthetic.main.fragment_food_planner.*
import org.koin.android.viewmodel.ext.android.viewModel


private const val TAG = "FoodPlannerFragment"

private const val DIALOG_EDIT_MEAL_ITEM = 1

class FoodPlannerFragment : Fragment(), MealPlanAdapter.OnMealPlanItemClickListener, EditMealItemDialog.EditMealDialogEvents {

    private var mAdapter: MealPlanAdapter? = null

    private val foodPlannerViewModel: FoodPlannerViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Log.d(TAG, "onCreateView starts")
        val root = inflater.inflate(R.layout.fragment_food_planner, container, false)
        Log.d(TAG, "onCreateView ends")
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        Log.d(TAG, "onViewCreated starts")

        recycler_meal_plan.layoutManager = LinearLayoutManager(context)

        //replaced RecyclerItemTouchHelper with external library
//        recycler_meal_plan.addItemDecoration(DividerItemDecoration(context, LinearLayoutManager.VERTICAL))
//        recycler_meal_plan.addOnItemTouchListener(RecyclerItemTouchHelper(requireContext(), recycler_meal_plan, this))


        Log.d(TAG, "onViewCreated ends")
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate: called")
        super.onCreate(savedInstanceState)

        foodPlannerViewModel.currentMealPlanLD.observe(this, { options ->
            Log.d(TAG, "onCreate: observing firestore adapter options with $options")

            mAdapter = MealPlanAdapter(options, this)

            recycler_meal_plan.adapter = mAdapter
            mAdapter?.startListening()

        })

        foodPlannerViewModel.selectedDateLD.observe(this, { date ->
            val dateFormat = DateFormat.getDateFormat(context) // formats date depending on different parts of a world
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

        foodPlannerViewModel.toastLD.observe(this, { message ->
            message?.let {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                foodPlannerViewModel.onToastShown()
            }
        })

        Log.d(TAG, "onCreate ends")
    }

    override fun onResume() {
        super.onResume()
        foodPlannerViewModel.updateCalories() // handle back button from FoodMenuActivity
    }

    override fun onStart() {
        Log.d(TAG, "onStart starts")
        super.onStart()
        mAdapter?.startListening()
        Log.d(TAG, "onStart ends")
    }

    override fun onStop() {
        Log.d(TAG, "onStop starts")
        super.onStop()
        mAdapter?.stopListening()
        Log.d(TAG, "onStop ends")
    }

    override fun onEditClick(mealPlanItem: MealPlanItem, viewHolder: RecyclerView.ViewHolder) {
        Log.d(TAG, "onEditClick called ${viewHolder.adapterPosition}, meal item $mealPlanItem")

        val dialog = EditMealItemDialog()
        val args = Bundle().apply {
            putInt(EDIT_MEAL_DIALOG_ID, DIALOG_EDIT_MEAL_ITEM)
            putParcelable(EDIT_MEAL_DIALOG_MEAL_ITEM, mealPlanItem)
            putInt(EDIT_MEAL_DIALOG_MEAL_ITEM_POSITION, viewHolder.adapterPosition)
        }
        dialog.arguments = args
        dialog.show(childFragmentManager, "editMealItem") //calling dialog from fragment -> childFragmentManager
    }

    override fun onEditDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onEditDialogResult: called with dialogId $dialogId")
        if (dialogId == DIALOG_EDIT_MEAL_ITEM) {
            val changedMealPlanItem = args.getParcelable(EDIT_MEAL_DIALOG_MEAL_ITEM) as MealPlanItem?
            val itemPosition = args.getInt(EDIT_MEAL_DIALOG_MEAL_ITEM_POSITION)
            Log.d(TAG, "onEditDialogResult: called with dialogId $dialogId, mealItem $changedMealPlanItem and $itemPosition")
            changedMealPlanItem?.let { mAdapter?.updateItem(it, itemPosition) }

            foodPlannerViewModel.updateCalories()
        }
        Log.d(TAG, "onEditDialogResult: ends with dialogId $dialogId")

    }

    override fun onDeleteClick(viewHolder: RecyclerView.ViewHolder) {
        Log.d(TAG, "onDeleteClick called ${viewHolder.adapterPosition}")
        mAdapter?.deleteItem(viewHolder.adapterPosition)
        foodPlannerViewModel.updateCalories()
    }

//    override fun onSwipe(viewHolder: RecyclerView.ViewHolder) {
//        Log.d(TAG, "onSwipe called ${viewHolder.adapterPosition}")
//        mAdapter?.deleteItem(viewHolder.adapterPosition)
//        foodPlannerViewModel.updateCalories()
//    }
//
//    override fun onItemDoubleClick(view: View, position: Int) {
//        Log.d(TAG, "onItemDoubleClick starts")
//        val mealPlanItem = mAdapter?.getItem(position)
//        Log.d(TAG, "onItemDoubleClick ends with $mealPlanItem")
//    }


}