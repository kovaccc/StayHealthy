package com.example.stayhealthy.di

import com.example.stayhealthy.repositories.implementation.FoodRepositoryImpl
import com.example.stayhealthy.repositories.implementation.MealPlanRepositoryImpl
import com.example.stayhealthy.repositories.implementation.UserRepositoryImpl
import org.koin.dsl.module


val repositoryModule = module {
    single { UserRepositoryImpl(get(), get(), get()) }
    single { MealPlanRepositoryImpl(get()) }
    single { FoodRepositoryImpl(get(), get()) }
}
