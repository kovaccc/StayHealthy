package com.example.stayhealthy.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stayhealthy.*
import com.example.stayhealthy.dialogs.*
import com.example.stayhealthy.pagerAdapter.MenuSlidePagerAdapter
import com.example.stayhealthy.model.Food
import com.example.stayhealthy.model.MealPlanItem
import com.example.stayhealthy.repository.MenuContract
import com.example.stayhealthy.ui.foodMenu.FoodMenuFragment
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_food_menu.*
import kotlinx.android.synthetic.main.content_food_menu.*
import org.koin.android.viewmodel.ext.android.viewModel

private const val TAG = "FoodMenuActivity"

private const val DIALOG_CHANGE_QUANTITY = 1

class FoodMenuActivity : AppCompatActivity(), FoodMenuFragment.OnFoodAdd, ChangeFoodQuantityDialog.ChangeQuantityDialogEvents {

    var date: Long? = null
    private var userId: String? = null

    private val mealPlanViewModel: MealPlanViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: starts")
        setContentView(R.layout.activity_food_menu)

        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (intent != null) {
            date = intent.extras?.getLong(MEAL_DATE_TRANSFER, DATE_DEFAULT)
            userId = intent.extras?.getString(CURRENT_USER_ID_TRANSFER)
        }


        mealPlanViewModel.toast.observe(this,{ message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                mealPlanViewModel.onToastShown()
            }
        })

        Log.d(TAG, "onCreate: intent date is $date, userid is $userId")
        setUpPager()
        Log.d(TAG, "onCreate: ends")
    }

    private fun setUpPager() {

        val menuNodesList = arrayListOf(
                MenuContract.FRUITS_NODE_NAME,
                MenuContract.GRAINS_PASTA_NODE_NAME,
                MenuContract.PROTEINS_NODE_NAME,
                MenuContract.VEGETABLES_NODE_NAME
        )

        val pagerAdapter = MenuSlidePagerAdapter(this, menuNodesList)
        viewPager_food_menu.adapter = pagerAdapter
        viewPager_food_menu.setPageTransformer(ZoomOutPageTransformer())

        TabLayoutMediator(tab_food_menu, viewPager_food_menu) { tab, position ->
            tab.text = pagerAdapter.getTitle(position)
        }.attach()

    }

    override fun onFoodAdd(foodItem: Food, category: String) {

        val dialog = ChangeFoodQuantityDialog()
        val args = Bundle().apply{
            putInt(CHANGE_QUANTITY_DIALOG_ID, DIALOG_CHANGE_QUANTITY)
            putParcelable(CHANGE_QUANTITY_DIALOG_FOOD, foodItem)
            putString(CHANGE_QUANTITY_DIALOG_FOOD_CATEGORY, category)
        }
        dialog.arguments = args
        dialog.show(supportFragmentManager, "changeFoodQuantity")

    }

    override fun onAddDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onAddDialogResult: called with dialogId $dialogId")
        if(dialogId == DIALOG_CHANGE_QUANTITY) {
            val food = args.getParcelable(CHANGE_QUANTITY_DIALOG_FOOD) as Food?
            val category = args.getString(CHANGE_QUANTITY_DIALOG_FOOD_CATEGORY)
            Log.d(TAG, "onAddDialogResult: called with dialogId $dialogId, user $userId, date $date with food item $food, category $category")

            val mealPlanItem = MealPlanItem(id = "", food?.Name!!, food.Quantity!!, food.Calories!!, category!!, date!!)
            mealPlanViewModel.createMealPlanItemInFirestore(userId!!, mealPlanItem, this@FoodMenuActivity)
        }
    }

}