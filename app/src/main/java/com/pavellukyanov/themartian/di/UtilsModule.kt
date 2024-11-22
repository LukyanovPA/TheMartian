package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.utils.ErrorQueue
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val utilsModule = module {
    singleOf(::ErrorQueue)
}