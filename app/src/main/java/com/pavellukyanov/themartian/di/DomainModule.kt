package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.domain.usecase.ChangeFavourites
import com.pavellukyanov.themartian.domain.usecase.DeleteCameraCache
import com.pavellukyanov.themartian.domain.usecase.DeleteOldCachedPhoto
import com.pavellukyanov.themartian.domain.usecase.DeleteRoverInfoCache
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.GetFavourites
import com.pavellukyanov.themartian.domain.usecase.GetPhotoById
import com.pavellukyanov.themartian.domain.usecase.GetRoversOnFavourites
import com.pavellukyanov.themartian.domain.usecase.IsEmptyRoverCache
import com.pavellukyanov.themartian.domain.usecase.IsRoverDataAvailable
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.domain.usecase.PhotoToCache
import com.pavellukyanov.themartian.domain.usecase.UpdateCamerasCache
import com.pavellukyanov.themartian.domain.usecase.UpdateRoverInfoCache
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factory { LoadRovers(roverInfoDao = get()) }
    factory { UpdateRoverInfoCache(roverInfoDao = get(), apiDataSource = get(), camerasDao = get()) }
    factory { GetPhotoById(photoDao = get()) }
    factory { ChangeFavourites(photoDao = get()) }
    factory { PhotoToCache(photoDao = get()) }
    factory { DeleteOldCachedPhoto(photoDao = get()) }
    factory { GetCameras(camerasDao = get()) }
    factory { UpdateCamerasCache(camerasDao = get()) }
    factory { LoadPhotos(apiDataSource = get(), updateCamerasCache = get()) }
    factory { DeleteRoverInfoCache(roverInfoDao = get()) }
    factory { DeleteCameraCache(camerasDao = get()) }
    factory { GetFavourites(photoDao = get()) }
    factory { GetRoversOnFavourites(photoDao = get()) }
    factory { IsEmptyRoverCache(roverInfoDao = get()) }
    factoryOf(::IsRoverDataAvailable)
}