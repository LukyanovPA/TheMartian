package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.ui.screens.home.HomeReducer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val reducerModule = module {
    viewModel { HomeReducer(loadRovers = get()) }
}