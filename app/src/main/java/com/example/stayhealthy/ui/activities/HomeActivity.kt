package com.example.stayhealthy.ui.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.stayhealthy.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import kotlinx.android.synthetic.main.content_parameter.*
import kotlinx.android.synthetic.main.nav_header_home.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import java.util.*
import android.view.Menu
import android.widget.TextView
import com.example.stayhealthy.data.PrefsHelper
import com.example.stayhealthy.data.models.domain.User
import com.example.stayhealthy.ui.dialogs.*
import com.example.stayhealthy.viewmodels.UserViewModel
import kotlinx.android.synthetic.main.content_home.*
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView


private const val TAG = "HomeActivity"

private const val DIALOG_FILTER = 1

internal const val MEAL_DATE_TRANSFER = "MEAL_DATE_TRANSFER" // use for intent extras
internal const val CURRENT_USER_ID_TRANSFER = "CURRENT_USER_ID_TRANSFER" // use for intent extras


class HomeActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener {


    private lateinit var appBarConfiguration: AppBarConfiguration

    private var mDate: Long? = null

    private var currentUser: User? = null

    private val prefsHelper: PrefsHelper by inject()
    private val userViewModel: UserViewModel by inject()

    override fun onCreate(savedInstanceState: Bundle?) {

        Log.d(TAG, "onCreate: starts")
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        mDate = prefsHelper.getSelectedMealPlanDate()

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@HomeActivity, FoodMenuActivity::class.java)
            intent.apply {
                putExtra(MEAL_DATE_TRANSFER, mDate)
                putExtra(CURRENT_USER_ID_TRANSFER, currentUser?.id)
            }
            startActivity(intent)
        }

        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(setOf(
                R.id.nav_food_planner, R.id.nav_my_profile, R.id.nav_knowledge_base,
                R.id.nav_log_out), drawerLayout)

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->

            when (destination.id) {
                R.id.nav_log_out -> {
                    userViewModel.logOutUser()
                    val intent = Intent(this@HomeActivity, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                R.id.nav_food_planner -> {
                    fab.show()
                }
                R.id.nav_my_profile -> {
                    fab.hide()
                }
                R.id.nav_knowledge_base -> {
                    fab.hide()
                }
            }
        }


        val header: View = navView.getHeaderView(0)
        val tvHeaderName: TextView = header.findViewById(R.id.tv_header_name)

        userViewModel.currentUserLD.observe(this, { user ->
            if (user != null) {
                Log.d(TAG, "onCreate: observing user with value ${user.name}")
                tvHeaderName.text = user.name
                currentUser = user

                if (prefsHelper.getUserFirstLogin(user.id)) {
                    showInstructions()
                    prefsHelper.saveUserFirstLogin(user.id, false)
                }
            }
        })

        Log.d(TAG, "onCreate: ends")
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_home, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId

        when (id) {
            R.id.home_filter_date -> {
                showDatePickerDialog(getString(R.string.home_title_filter_date), DIALOG_FILTER)
                return true
            }

        }
        return super.onOptionsItemSelected(item)
    }


    private fun showInstructions() {

        MaterialShowcaseView.Builder(this)
                .setTarget(empty_view)
                .setDismissText(getString(R.string.got_it))
                .setContentText(R.string.foodplanner_instructions_heading)
                .setContentText(R.string.foodplanner_instructions_description)
                .setDelay(500) // optional but starting animations immediately in onCreate can make them choppy
//           .singleUse(SHOWCASE_ID) // provide a unique ID used to ensure it is only shown once
                .show()
    }

    private fun showDatePickerDialog(title: String, dialogId: Int) {
        val dialogFragment = DatePickerFragment()

        val arguments = Bundle()
        arguments.putInt(DATE_PICKER_ID, dialogId)
        arguments.putString(DATE_PICKER_TITLE, title)

        val date = Date()

        mDate?.let {
            date.time = it
        }
        arguments.putSerializable(DATE_PICKER_DATE, date)

        dialogFragment.arguments = arguments
        dialogFragment.show(supportFragmentManager, "datePicker")
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    override fun onResume() {
        Log.d(TAG, "onResume: starts")
        super.onResume()
        Log.d(TAG, "onResume: ends")
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        Log.d(TAG, "onDateSet: starts")
        val dialogId = view.tag as Int //dialog id in tag in datepickerfragment, views tag is general purpose slot where you can store things, we can store any object here
        when (dialogId) {
            DIALOG_FILTER -> {
                val calendar = GregorianCalendar()
                calendar.set(year, month, dayOfMonth, 0, 0, 0)
                prefsHelper.saveSelectedMealPlanDate(calendar.timeInMillis)
                mDate = calendar.timeInMillis
            }

            else -> throw IllegalArgumentException("Invalid mode when receiving DatePickerDialog result")
        }
        Log.d(TAG, "onDateSet: ends")
    }


    override fun onDestroy() {
        Log.d(TAG, "onDestroy: starts")
        super.onDestroy()
        Log.d(TAG, "onDestroy: ends")
    }
}