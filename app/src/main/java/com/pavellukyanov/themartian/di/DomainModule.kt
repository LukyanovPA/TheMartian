package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.domain.usecase.ChangeFavourites
import com.pavellukyanov.themartian.domain.usecase.GetLatestPhotos
import com.pavellukyanov.themartian.domain.usecase.IsFavourites
import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.domain.usecase.UpdateRoverInfoCache
import com.pavellukyanov.themartian.domain.utils.PhotoStorage
import com.pavellukyanov.themartian.domain.utils.Storage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val domainModule = module {
    single { LoadRovers(roverInfoDao = get()) }
    single { UpdateRoverInfoCache(roverInfoDao = get(), apiDataSource = get()) }
    single { GetLatestPhotos(apiDataSource = get()) }
    single<Storage<PhotoDto?>>(named("PHOTO_STORAGE")) { PhotoStorage() }
    single { IsFavourites(favouritesDao = get()) }
    single { ChangeFavourites(favouritesDao = get()) }
}