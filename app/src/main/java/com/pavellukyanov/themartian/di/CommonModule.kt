package com.pavellukyanov.themartian.di

import com.pavellukyanov.themartian.common.NetworkMonitor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val commonModule = module {
    factory { NetworkMonitor(context = androidContext()) }
}