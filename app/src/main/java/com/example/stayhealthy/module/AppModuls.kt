package com.example.stayhealthy.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.stayhealthy.repository.implementation.FoodRepositoryImpl
import com.example.stayhealthy.repository.implementation.MealPlanRepositoryImpl
import com.example.stayhealthy.repository.implementation.UserRepositoryImpl
import com.example.stayhealthy.ui.foodMenu.FoodMenuViewModel
import com.example.stayhealthy.ui.MealPlanViewModel
import com.example.stayhealthy.ui.profile.ProfileViewModel
import com.example.stayhealthy.ui.UserViewModel
import com.example.stayhealthy.ui.foodPlanner.FoodPlannerViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module



// here we define how to create these dependencies
val viewModelModule = module {

    single { UserViewModel(UserRepositoryImpl()) } // singleton created only once in lifecycle / one instance in the whole app

    viewModel{ FoodPlannerViewModel(get(named("datePrefs")), MealPlanRepositoryImpl(), UserRepositoryImpl())} //inject it in constructor like this or you can extend Koin component in view model and use it with by inject
    // this viewmodel scope bind viewmodel component to android component lifecycle, it can also be shared across fragments

    viewModel{ ProfileViewModel(UserRepositoryImpl())}

    viewModel { FoodMenuViewModel(FoodRepositoryImpl()) }

    viewModel { MealPlanViewModel(MealPlanRepositoryImpl()) }

    // factory{}
}

val repositoryModule = module {
    single {UserRepositoryImpl()}
    single{ MealPlanRepositoryImpl()}
    single { FoodRepositoryImpl()}
}

val preferencesModule = module {
    single(named("datePrefs")) { provideDatePreferences(androidApplication()) }

    single(named("appStartPrefs")) { provideAppStartingPreferences(androidApplication())}
}


private const val DATE_PREFERENCES_FILE_KEY = "com.example.date_preferences"
private fun provideDatePreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(DATE_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)

// keys
const val DATE_MEAL_PLAN = "MealPlanDate" // use for datePrefs



private const val APP_START_PREFERENCES_FILE_KEY = "com.example.app_start_preferences"
private fun provideAppStartingPreferences(app: Application): SharedPreferences =
        app.getSharedPreferences(APP_START_PREFERENCES_FILE_KEY, Context.MODE_PRIVATE)

// keys
const val APP_START_FIRST_LOGIN = "FirstLogin" // use for appStartPrefs



