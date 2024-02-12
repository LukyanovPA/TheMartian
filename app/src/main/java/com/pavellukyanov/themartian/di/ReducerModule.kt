package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.MainActivityReducer
import com.pavellukyanov.themartian.ui.screens.gallery.GalleryReducer
import com.pavellukyanov.themartian.ui.screens.home.HomeReducer
import com.pavellukyanov.themartian.ui.screens.photo.PhotoReducer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val reducerModule = module {
    viewModel { MainActivityReducer() }
    viewModel { HomeReducer(loadRovers = get()) }
    viewModel { GalleryReducer(photoToCache = get(), getCameras = get(), loadPhotos = get()) }
    viewModel { PhotoReducer(getPhotoById = get(), changeFavourites = get()) }
}