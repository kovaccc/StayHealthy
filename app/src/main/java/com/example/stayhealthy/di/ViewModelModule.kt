package com.example.stayhealthy.di

import com.example.stayhealthy.repositories.implementation.FoodRepositoryImpl
import com.example.stayhealthy.repositories.implementation.MealPlanRepositoryImpl
import com.example.stayhealthy.repositories.implementation.UserRepositoryImpl
import com.example.stayhealthy.viewmodels.*
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module


val viewModelModule = module {
    single { UserViewModel(UserRepositoryImpl(get(), get(), get()), get()) }
    viewModel {
        FoodPlannerViewModel(
                get(named("datePrefs")),
                get(),
                MealPlanRepositoryImpl(get()),
                UserRepositoryImpl(get(), get(), get())
        )
    }
    viewModel { ProfileViewModel(UserRepositoryImpl(get(), get(), get())) }
    viewModel { FoodMenuViewModel(FoodRepositoryImpl(get())) }
    viewModel { MealPlanViewModel(MealPlanRepositoryImpl(get())) }
}
