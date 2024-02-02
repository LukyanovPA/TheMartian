package com.pavellukyanov.themartian.di

import androidx.room.Room
import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.cache.MartianLocalDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule = module {
    //Api
    single { ApiDataSource(roverService = get()) }

    //Cache
    single {
        Room.databaseBuilder(
            androidApplication(),
            MartianLocalDatabase::class.java,
            "MartianLocalDatabase.db"
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    single { get<MartianLocalDatabase>().roverInfo() }
    single { get<MartianLocalDatabase>().favourites() }
}