package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.utils.helpers.DatabaseHelper
import com.pavellukyanov.themartian.utils.helpers.ImageLoaderHelper
import com.pavellukyanov.themartian.utils.helpers.SharedPreferencesHelper
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val helpersModule = module {
    factoryOf(::DatabaseHelper)
    factoryOf(::SharedPreferencesHelper)
    factoryOf(::ImageLoaderHelper)
}