package com.example.stayhealthy.ui.dialogs

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDialogFragment
import com.example.stayhealthy.R
import com.example.stayhealthy.data.models.domain.MealPlanItem
import kotlinx.android.synthetic.main.layout_edit_meal_item_dialog.*
import kotlinx.android.synthetic.main.layout_edit_meal_item_dialog.view.*


private const val TAG = "UpdateMealItemDialog"

const val EDIT_MEAL_DIALOG_ID = "ID"
const val EDIT_MEAL_DIALOG_MEAL_ITEM = "MEAL ITEM"
const val EDIT_MEAL_DIALOG_MEAL_ITEM_POSITION = "MEAL ITEM POSITION"

class EditMealItemDialog : AppCompatDialogFragment() {

    private var mealPosition: Int? = null
    private var factor: Double? = null
    private var mealItem: MealPlanItem? = null
    private var dialogId = 0

    private var dialogEvents: EditMealDialogEvents? = null

    interface EditMealDialogEvents {
        fun onEditDialogResult(dialogId: Int, args: Bundle)
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.EditMealItemDialogStyle)

        val arguments = arguments

        if (arguments != null) {

            dialogId = arguments.getInt(EDIT_MEAL_DIALOG_ID)

            mealItem = arguments.getParcelable(EDIT_MEAL_DIALOG_MEAL_ITEM) as MealPlanItem?

            mealPosition = arguments.getInt(EDIT_MEAL_DIALOG_MEAL_ITEM_POSITION)

        }

        val calories = mealItem?.calories ?: 0
        val quantity = mealItem?.quantity ?: 0

        // factor with which are grams multiplied to get appropriate calories
        factor = calories.toDouble() / quantity

        Log.d(
                TAG,
                "onCreate: ends, arguments: dialog id $dialogId, food item $mealItem, and calculated factor $factor"
        )

    }


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        Log.d(TAG, "onCreateView: starts")
        val root = inflater.inflate(R.layout.layout_edit_meal_item_dialog, container, false)

        root.tvMealItemName?.text = mealItem?.name
        root.etAddQuantity?.setText(mealItem?.quantity.toString())
        root.tvCalories?.text = mealItem?.calories.toString()

        Log.d(TAG, "onCreateView: ends")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        Log.d(TAG, "onViewCreated: starts")
        super.onViewCreated(view, savedInstanceState)
        dialog?.setTitle(R.string.action_edit_meal_item)

        okButton.setOnClickListener {

            if (etAddQuantity.text?.isEmpty() == true) {
                etAddQuantity.error = getString(R.string.error_valid_quantity)
            } else {

                val changedMealItem = MealPlanItem(
                        mealItem?.id ?: "",
                        mealItem?.name ?: "",
                        etAddQuantity.text.toString().toLong(),
                        tvCalories.text.toString().toLong(),
                        mealItem?.category ?: "",
                        mealItem?.date ?: DATE_DEFAULT
                )

                val args = Bundle().apply {
                    putParcelable(EDIT_MEAL_DIALOG_MEAL_ITEM, changedMealItem)
                    mealPosition?.let { position -> putInt(EDIT_MEAL_DIALOG_MEAL_ITEM_POSITION, position) }
                }
                Log.d(TAG, "setOnClickListener: starts with $args")

                (parentFragment as EditMealDialogEvents).onEditDialogResult(dialogId, args)

                dismiss()
            }
        }

        cancelButton.setOnClickListener { dismiss() }


        etAddQuantity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.isNotEmpty()) {
                    tvCalories?.text = (s.toString().toLong() * factor!!).toInt()
                            .toString() // no need for double value to show in textview
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
        dialogEvents = try {
            // Is there a parent fragment? If so, that will be what we call back
            parentFragment as EditMealDialogEvents
        } catch (e: TypeCastException) {
            try {
                // No parent fragment, so call back the Activity instead
                context as EditMealDialogEvents
            } catch (e: ClassCastException) {
                // Activity doesn't implement the interface
                throw ClassCastException("Activity $context must implement EditMealItemDialog.EditMealDialogEvents interface")
            }
        } catch (e: ClassCastException) {
            // Parent fragment doesn't implement the interface
            throw ClassCastException("Fragment $parentFragment must implement EditMealItemDialog.EditMealDialogEvents interface")
        }

    }

    override fun onDetach() {
        Log.d(TAG, "onDetach called")
        super.onDetach()

    }
}