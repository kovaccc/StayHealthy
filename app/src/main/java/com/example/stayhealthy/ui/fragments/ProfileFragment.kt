package com.example.stayhealthy.ui.fragments


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.stayhealthy.R
import com.example.stayhealthy.data.models.domain.User
import com.example.stayhealthy.viewmodels.ProfileViewModel
import kotlinx.android.synthetic.main.fragment_my_profile.*
import kotlinx.android.synthetic.main.fragment_my_profile.etAge
import kotlinx.android.synthetic.main.fragment_my_profile.etHeight
import kotlinx.android.synthetic.main.fragment_my_profile.etName
import kotlinx.android.synthetic.main.fragment_my_profile.etWeight
import kotlinx.android.synthetic.main.fragment_my_profile.spinnerActivityLevel
import kotlinx.android.synthetic.main.fragment_my_profile.view.*
import org.koin.android.viewmodel.ext.android.viewModel
import java.util.*


private const val TAG = "ProfileFragment"

class ProfileFragment : Fragment() {

    private var currentUser: User? = null

    private var adapter: ArrayAdapter<CharSequence?>? = null

    private var ignoreChanges: Boolean = false

    private var timer: Timer? = null

    private val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {

        Log.d(TAG, "onCreateView starts")
        val root = inflater.inflate(R.layout.fragment_my_profile, container, false)

        root.rgGender_profile_fragment.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.rbMale_profile_fragment -> {
                    updateUser()
                }
                R.id.rbFemale_profile_fragment -> {
                    updateUser()
                }
            }
        }

        root.ivSpinnerArrow.setOnClickListener { spinnerActivityLevel.performClick() }

        root.spinnerActivityLevel.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                    ) {
                        updateUser()
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                    }
                }

        onTextChangedListener(root.etName)
        onTextChangedListener(root.etAge)
        onTextChangedListener(root.etHeight)
        onTextChangedListener(root.etWeight)
        onTextChangedListener(root.etName)

        Log.d(TAG, "onCreateView ends")

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "onViewCreated starts")
        setActivityLevelAdapter()
        Log.d(TAG, "onViewCreated ends")
    }


    private fun setActivityLevelAdapter() {

        adapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.activity_level_arrays,
                R.layout.spinner_item
        )
        adapter?.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerActivityLevel.adapter = adapter

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.d(TAG, "onCreate starts")
        super.onCreate(savedInstanceState)


        profileViewModel.currentUserLD.observe(this, { user ->
            Log.d(
                    TAG,
                    "onCreate: observing user with value ${user?.name} and ignoreChanges $ignoreChanges"
            )

            if (!ignoreChanges) {
                etName.setText(user?.name)
                etAge.setText(user?.age.toString())
                etWeight.setText(user?.weight.toString())
                etHeight.setText(user?.height.toString())
                val spinnerPosition = adapter?.getPosition(user?.activityLevel)
                spinnerPosition?.let { spinnerActivityLevel.setSelection(it) }
                when (user?.gender) {
                    getString(R.string.sex_male) -> {
                        rbMale_profile_fragment.isChecked = true
                    }
                    getString(R.string.sex_female) -> {
                        rbFemale_profile_fragment.isChecked = true
                    }
                }
            }

            ignoreChanges =
                    true // after first observing with live data there is no need to observe changes in editText / also avoiding infinite loop with editText on change
            currentUser = user?.asDomain()
        })

        profileViewModel.currentUserCalorieNeedsLD.observe(this, { calories ->
            tvUserCalorieNeedsValue.text =
                    getString(R.string.food_calorie_value, calories.toString())
        })

        profileViewModel.currentUserBMILD.observe(this, { valueBMI ->
            tvBMIValue.text = valueBMI.toString()
        })


        profileViewModel.toast.observe(this, { message ->
            message?.let {
                Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
                profileViewModel.onToastShown()
            }
        })

        Log.d(TAG, "onCreate ends")
    }

    private fun onTextChangedListener(editText: EditText) { //updating every 600ms when editText changes

        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // user is typing: reset already started timer (if existing)
                timer?.cancel()

            }

            override fun afterTextChanged(s: Editable?) {
                // user typed: start the timer
                timer = Timer()
                timer?.schedule(object : TimerTask() {
                    override fun run() {
                        if (editText.text.isNotEmpty()) {
                            updateUser()
                        }
                    }
                }, 600) // 600ms delay before the timer executes the „run“ method from TimerTask
            }
        })
    }

    private fun updateUser() {
        if (currentUser != null) {
            lifecycleScope.launchWhenResumed {
                if (isParametersValid()) {
                    val user =
                            userFromUi() // with lifecycle.launchWhenResumed it will only execute when it get in resume state so you fix crashing with etName == null
                    if (user != currentUser) { // check if user is the same
                        profileViewModel.apply {
                            updateUserInFirestore(user, requireActivity())
                            getUserBMI(user)
                            getUserCalorieNeeds(user)
                        }
                    }
                }
            }
        }
    }

    private fun isParametersValid(): Boolean {
        return etName.text.isNotEmpty()
                && etAge.text.isNotEmpty()
                && etHeight.text.isNotEmpty()
                && etWeight.text.isNotEmpty()
    }

    private fun userFromUi(): User {
        val mGender = checkSex()
        return User(
                currentUser!!.id,
                etName.text.toString(),
                mGender,
                etAge.text.toString().toLong(),
                etHeight.text.toString().toLong(),
                etWeight.text.toString().toLong(),
                spinnerActivityLevel.selectedItem.toString()
        )
    }

    private fun checkSex(): String {
        Log.d(TAG, "checkSex: starting")
        return if (rbMale_profile_fragment.isChecked) {
            getString(R.string.sex_male)
        } else {
            getString(R.string.sex_female)
        }
    }
}