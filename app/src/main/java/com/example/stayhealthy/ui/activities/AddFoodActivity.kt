package com.example.stayhealthy.ui.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.stayhealthy.R
import com.example.stayhealthy.common.extensions.setImageWithURI
import com.example.stayhealthy.config.ADD_FOOD_URI
import com.example.stayhealthy.databinding.ActivityAddFoodBinding
import com.example.stayhealthy.ui.dialogs.DialogHelper
import com.example.stayhealthy.util.ConnectionManager
import com.example.stayhealthy.viewmodels.FoodMenuViewModel
import kotlinx.android.synthetic.main.activity_add_food.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.viewModel

class AddFoodActivity : AppCompatActivity() {

    private val foodMenuViewModel: FoodMenuViewModel by viewModel()
    private lateinit var binding: ActivityAddFoodBinding
    private val foodFileUri get() = intent.extras?.getParcelable<Uri>(ADD_FOOD_URI)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
                DataBindingUtil.setContentView<ActivityAddFoodBinding>(this, R.layout.activity_add_food)
                        .apply {
                            viewModel = foodMenuViewModel
                            lifecycleOwner = this@AddFoodActivity
                            executePendingBindings()
                        }


        foodFileUri?.let {
            imageViewFood.setImageWithURI(it)
        }

        buttonAddFood.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                if (ConnectionManager.isInternetAvailable()) {
                    val dialog = DialogHelper.createLoadingDialog(this@AddFoodActivity)
                    dialog.show()
                    foodFileUri?.let { foodFileUri -> foodMenuViewModel.addUserFood(foodFileUri) }
                    dialog.dismiss()
                    finish()
                } else {
                    Toast.makeText(
                            this@AddFoodActivity,
                            getString(R.string.check_your_connection),
                            Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }

        foodMenuViewModel.toast.observe(this, { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        })

        setCategorySpinnerAdapter()
    }

    private fun setCategorySpinnerAdapter() {
        val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(
                this,
                R.array.food_categories,
                R.layout.spinner_item
        )
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        binding.spCategory.adapter = adapter
    }

}