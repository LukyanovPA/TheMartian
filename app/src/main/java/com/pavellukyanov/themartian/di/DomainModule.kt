package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import org.koin.dsl.module

val domainModule = module {
    single { LoadRovers(apiDataSource = get()) }
}