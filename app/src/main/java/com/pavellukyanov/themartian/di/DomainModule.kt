package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.domain.usecase.ChangeFavourites
import com.pavellukyanov.themartian.domain.usecase.DeleteCameraCache
import com.pavellukyanov.themartian.domain.usecase.DeleteOldCachedPhoto
import com.pavellukyanov.themartian.domain.usecase.DeleteRoverInfoCache
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.GetFavourites
import com.pavellukyanov.themartian.domain.usecase.GetPhotoById
import com.pavellukyanov.themartian.domain.usecase.GetPhotoFromApi
import com.pavellukyanov.themartian.domain.usecase.GetRoversOnFavourites
import com.pavellukyanov.themartian.domain.usecase.IsEmptyRoverCache
import com.pavellukyanov.themartian.domain.usecase.IsRoverDataAvailable
import com.pavellukyanov.themartian.domain.usecase.LoadLocations
import com.pavellukyanov.themartian.domain.usecase.LoadPanoramas
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.LoadRoverManifest
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
    factory { LoadPhotos(apiDataSource = get()) }
    factory { DeleteRoverInfoCache(roverInfoDao = get()) }
    factory { DeleteCameraCache(camerasDao = get()) }
    factory { GetFavourites(photoDao = get()) }
    factory { GetRoversOnFavourites(photoDao = get()) }
    factory { IsEmptyRoverCache(roverInfoDao = get()) }
    factoryOf(::IsRoverDataAvailable)
    // New use cases
    factory { GetPhotoFromApi(apiDataSource = get(), photoDao = get()) }
    factory { LoadPanoramas(apiDataSource = get()) }
    factory { LoadLocations(apiDataSource = get()) }
    factory { LoadRoverManifest(apiDataSource = get()) }
}
