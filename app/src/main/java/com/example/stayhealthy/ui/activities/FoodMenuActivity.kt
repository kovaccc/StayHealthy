package com.example.stayhealthy.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import com.example.stayhealthy.*
import com.example.stayhealthy.ui.dialogs.*
import com.example.stayhealthy.ui.adapters.MenuSlidePagerAdapter
import com.example.stayhealthy.data.models.domain.Food
import com.example.stayhealthy.data.models.domain.MealPlanItem
import com.example.stayhealthy.common.contracts.MenuContract
import com.example.stayhealthy.common.extensions.hasPermissions
import com.example.stayhealthy.common.extensions.toast
import com.example.stayhealthy.config.ADD_FOOD_URI
import com.example.stayhealthy.config.CAMERA_PERMISSION
import com.example.stayhealthy.config.CAMERA_REQUEST_CODE
import com.example.stayhealthy.ui.fragments.FoodMenuFragment
import com.example.stayhealthy.util.ZoomOutPageTransformer
import com.example.stayhealthy.viewmodels.FoodMenuViewModel
import com.example.stayhealthy.viewmodels.MealPlanViewModel
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.android.synthetic.main.activity_food_menu.*
import kotlinx.android.synthetic.main.content_food_menu.*
import org.koin.android.viewmodel.ext.android.viewModel

private const val TAG = "FoodMenuActivity"

private const val DIALOG_CHANGE_QUANTITY = 1

class FoodMenuActivity : AppCompatActivity(), FoodMenuFragment.OnFoodAdd,
        ChangeFoodQuantityDialog.ChangeQuantityDialogEvents {

    private var date: Long? = null
    private var userId: String? = null

    private val mealPlanViewModel: MealPlanViewModel by viewModel()
    private val foodMenuViewModel: FoodMenuViewModel by viewModel()

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


        mealPlanViewModel.toastLD.observe(this, { message ->
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

        tab_food_menu.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
               foodMenuViewModel.getFoodOptionsCategory(category = menuNodesList[tab?.position!!])
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                //NOOP
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                //NOOP
            }

        })

    }

    override fun onFoodAdd(foodItem: Food, category: String) {

        val dialog = ChangeFoodQuantityDialog()
        val args = Bundle().apply {
            putInt(CHANGE_QUANTITY_DIALOG_ID, DIALOG_CHANGE_QUANTITY)
            putParcelable(CHANGE_QUANTITY_DIALOG_FOOD, foodItem)
            putString(CHANGE_QUANTITY_DIALOG_FOOD_CATEGORY, category)
        }
        dialog.arguments = args
        dialog.show(supportFragmentManager, "changeFoodQuantity")

    }

    override fun onAddDialogResult(dialogId: Int, args: Bundle) {
        Log.d(TAG, "onAddDialogResult: called with dialogId $dialogId")
        if (dialogId == DIALOG_CHANGE_QUANTITY) {
            val food = args.getParcelable(CHANGE_QUANTITY_DIALOG_FOOD) as Food?
            val category = args.getString(CHANGE_QUANTITY_DIALOG_FOOD_CATEGORY)
            Log.d(
                    TAG,
                    "onAddDialogResult: called with dialogId $dialogId, user $userId, date $date with food item $food, category $category"
            )

            food?.let {
                val mealPlanItem = MealPlanItem(
                        id = "",
                        food.Name,
                        food.Quantity,
                        food.Calories,
                        category ?: "",
                        date ?: 0
                )
                mealPlanViewModel.createMealPlanItemInFirestore(userId ?: "", mealPlanItem)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_food, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.menu_add_food -> {
                takePictureOfFoodIntent()
            }
            R.id.menu_app_food -> {
                foodMenuViewModel.isUserFoodMLD.value = false
                toolbar.title = getString(R.string.app_food)
            }
            R.id.menu_my_food -> {
                foodMenuViewModel.isUserFoodMLD.value = true
                toolbar.title = getString(R.string.my_food)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun takePictureOfFoodIntent() {
        val permissions = arrayOf(CAMERA_PERMISSION)
        if (this@FoodMenuActivity.hasPermissions(permissions)) {

            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            foodMenuViewModel.createImageFileFood()
            foodMenuViewModel.photoFoodFile?.also { file ->
                val photoURI: Uri = if (
                        Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    FileProvider.getUriForFile(
                            this,
                            BuildConfig.APPLICATION_ID,
                            file
                    )
                } else {
                    Uri.fromFile(foodMenuViewModel.photoFoodFile)
                }

                takePictureIntent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        photoURI
                )
                startActivityForResult(
                        takePictureIntent,
                        CAMERA_REQUEST_CODE
                )
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        permissions,
                        CAMERA_REQUEST_CODE
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                CAMERA_REQUEST_CODE -> {
                    startAddFoodActivity(bundleOf(ADD_FOOD_URI to Uri.fromFile(foodMenuViewModel.photoFoodFile)))
                }
            }
        }
    }

    private fun startAddFoodActivity(arguments: Bundle) {
        val intent = Intent(this, AddFoodActivity::class.java)
        intent.putExtras(arguments)
        startActivity(intent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (!((grantResults.isNotEmpty() &&
                                grantResults[0] == PackageManager.PERMISSION_GRANTED))
                ) {
                    toast(R.string.accept_permissions)
                } else {
                    val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

                    foodMenuViewModel.createImageFileFood()
                    foodMenuViewModel.photoFoodFile?.also { file ->
                        val photoURI: Uri = if (
                                Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                            FileProvider.getUriForFile(
                                    this,
                                    BuildConfig.APPLICATION_ID,
                                    file
                            )
                        } else {
                            Uri.fromFile(foodMenuViewModel.photoFoodFile)
                        }
                        takePictureIntent.putExtra(
                                MediaStore.EXTRA_OUTPUT,
                                photoURI
                        )
                        startActivityForResult(
                                takePictureIntent,
                                CAMERA_REQUEST_CODE
                        )
                    }
                }
            }
        }
    }


}