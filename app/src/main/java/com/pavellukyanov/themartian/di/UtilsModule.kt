package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.utils.ErrorQueue
import com.pavellukyanov.themartian.utils.GalleryBrowseSession
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val utilsModule = module {
    singleOf(::ErrorQueue)
    singleOf(::GalleryBrowseSession)
}