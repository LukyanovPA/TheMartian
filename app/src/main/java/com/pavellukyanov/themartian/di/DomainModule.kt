package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.domain.usecase.ChangeFavourites
import com.pavellukyanov.themartian.domain.usecase.DeleteOldCachedPhoto
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.GetPhotoById
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.domain.usecase.PhotoToCache
import com.pavellukyanov.themartian.domain.usecase.UpdateCamerasCache
import com.pavellukyanov.themartian.domain.usecase.UpdateRoverInfoCache
import org.koin.dsl.module

val domainModule = module {
    single { LoadRovers(roverInfoDao = get()) }
    single { UpdateRoverInfoCache(roverInfoDao = get(), apiDataSource = get(), camerasDao = get()) }
    single { GetPhotoById(photoDao = get()) }
    single { ChangeFavourites(photoDao = get()) }
    single { PhotoToCache(photoDao = get()) }
    single { DeleteOldCachedPhoto(photoDao = get()) }
    single { GetCameras(camerasDao = get()) }
    single { UpdateCamerasCache(camerasDao = get()) }
    single { LoadPhotos(apiDataSource = get(), updateCamerasCache = get()) }
}