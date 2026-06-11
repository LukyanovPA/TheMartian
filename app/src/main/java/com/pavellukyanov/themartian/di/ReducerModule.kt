package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.MainActivityReducer
import com.pavellukyanov.themartian.ui.screens.gallery.GalleryReducer
import com.pavellukyanov.themartian.ui.screens.home.HomeReducer
import com.pavellukyanov.themartian.ui.screens.location.LocationReducer
import com.pavellukyanov.themartian.ui.screens.panorama.PanoramaReducer
import com.pavellukyanov.themartian.ui.screens.photo.PhotoReducer
import com.pavellukyanov.themartian.ui.screens.rover.RoverReducer
import com.pavellukyanov.themartian.ui.screens.splash.SplashReducer
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val reducerModule = module {
    viewModelOf(::MainActivityReducer)
    viewModelOf(::HomeReducer)
    viewModelOf(::GalleryReducer)
    viewModelOf(::PhotoReducer)
    viewModelOf(::SplashReducer)
    viewModelOf(::RoverReducer)
    viewModelOf(::PanoramaReducer)
    viewModelOf(::LocationReducer)
}
