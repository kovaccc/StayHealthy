package com.example.stayhealthy


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.stayhealthy.model.Food

import kotlinx.android.synthetic.main.layout_quantity_dialog.*
import kotlinx.android.synthetic.main.layout_quantity_dialog.view.*


private const val TAG = "ChangeFoodQuantity"

const val CHANGE_QUANTITY_ID = "ID"
const val CHANGE_QUANTITY_FOOD = "CHANGE FOOD"
const val CHANGE_QUANTITY_FOOD_CATEGORY = "FOOD CATEGORY"


class ChangeFoodQuantityDialog : AppCompatDialogFragment() {



    private var foodCategory: String? = null
    private var factor: Double? = null
    private var foodItem: Food? = null
    private var dialogId = 0

    interface ChangeQuantityDialogEvents {
        fun onAddDialogResult(dialogId: Int, args: Bundle)
    }



    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.ChangeFoodQuantityDialogStyle)

        val arguments = arguments

        if(arguments != null) {

            dialogId = arguments.getInt(CHANGE_QUANTITY_ID)

            foodItem = arguments.getParcelable(CHANGE_QUANTITY_FOOD) as Food?

            foodCategory = arguments.getString(CHANGE_QUANTITY_FOOD_CATEGORY)

        }

        val calories = foodItem?.Calories
        val quantity = foodItem?.Quantity

        // factor with which are grams multiplied to get appropriate calories
        factor = calories!!.toDouble() / quantity!!

        Log.d(TAG, "onCreate: ends, arguments: dialog id $dialogId, food item $foodItem, and calculated factor $factor")

    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")
        val root = inflater.inflate(R.layout.layout_quantity_dialog, container, false)


        root.tvFoodItemName?.text = foodItem?.Name
        root.etAddQuantity?.setText(foodItem?.Quantity.toString())
        root.tvCalories?.text = foodItem?.Calories.toString()

        Log.d(TAG, "onCreateView: ends")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: starts")
        super.onViewCreated(view, savedInstanceState)
        dialog?.setTitle(R.string.action_change_quantity)

        okButton.setOnClickListener {

            if(etAddQuantity.text?.isEmpty()!!) {
                etAddQuantity.error = getString(R.string.error_valid_quantity)
            }
            else {

            val changedFoodItem = Food(foodItem?.Name!!, foodItem?.Image!!, etAddQuantity.text.toString().toLong(), tvCalories.text.toString().toLong())

            val args = Bundle().apply {
                putParcelable(CHANGE_QUANTITY_FOOD, changedFoodItem)
                putString(CHANGE_QUANTITY_FOOD_CATEGORY, foodCategory)
            }
            Log.d(TAG, "setOnClickListener: starts with $args")

            (activity as ChangeQuantityDialogEvents).onAddDialogResult(dialogId, args)

            dismiss()
            }
        }

        cancelButton.setOnClickListener { dismiss()}


        etAddQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    tvCalories?.text = (s.toString().toLong() * factor!!).toInt().toString() // no need for double value to show in textview
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        Log.d(TAG, "onViewCreated: ends")

    }


    override fun onAttach(context: Context) {
        Log.d(TAG, "onAttach called: context is $context")
        super.onAttach(context)

        // Activities/Fragments containing this fragment must implement its callbacks.
        if (context !is ChangeQuantityDialogEvents) {
            throw RuntimeException("$context must implement ChangeFoodQuantityDialog.ChangeQuantityDialogEvents interface")
        }
    }

    override fun onDetach() {
        Log.d(TAG, "onDetach called")
        super.onDetach()

    }

}