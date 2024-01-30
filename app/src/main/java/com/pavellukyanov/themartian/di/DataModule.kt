package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.data.api.ApiDataSource
import org.koin.dsl.module

val dataModule = module {
    single { ApiDataSource(roverService = get()) }
}