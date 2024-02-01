package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.MainActivityReducer
import com.pavellukyanov.themartian.ui.screens.gallery.GalleryReducer
import com.pavellukyanov.themartian.ui.screens.home.HomeReducer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val reducerModule = module {
    viewModel { MainActivityReducer() }
    viewModel { HomeReducer(loadRovers = get()) }
    viewModel { GalleryReducer(getLatestPhotos = get()) }
}