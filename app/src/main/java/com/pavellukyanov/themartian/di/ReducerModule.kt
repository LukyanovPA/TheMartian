package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.MainActivityReducer
import com.pavellukyanov.themartian.ui.screens.gallery.GalleryReducer
import com.pavellukyanov.themartian.ui.screens.home.HomeReducer
import com.pavellukyanov.themartian.ui.screens.photo.PhotoReducer
import com.pavellukyanov.themartian.ui.screens.splash.SplashReducer
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val reducerModule = module {
    viewModel {
        MainActivityReducer(
            deleteOldCachedPhoto = get(),
            deleteRoverInfoCache = get(),
            deleteCameraCache = get(),
            updateRoverInfoCache = get(),
            isEmptyRoverCache = get()
        )
    }
    viewModel { HomeReducer(loadRovers = get()) }
    viewModel { GalleryReducer(photoToCache = get(), getCameras = get(), loadPhotos = get(), getFavourites = get(), getRoversOnFavourites = get()) }
    viewModel { PhotoReducer(getPhotoById = get(), changeFavourites = get()) }
    viewModel { SplashReducer(loadRovers = get()) }
}