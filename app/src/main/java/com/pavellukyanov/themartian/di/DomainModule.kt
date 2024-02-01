package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.domain.usecase.GetLatestPhotos
import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.domain.usecase.UpdateRoverInfoCache
import org.koin.dsl.module

val domainModule = module {
    single { LoadRovers(roverInfoDao = get()) }
    single { UpdateRoverInfoCache(roverInfoDao = get(), apiDataSource = get()) }
    single { GetLatestPhotos(apiDataSource = get()) }
}