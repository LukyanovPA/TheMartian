package com.pavellukyanov.themartian.di

import androidx.room.Room
import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.cache.MartianLocalDatabase
import com.pavellukyanov.themartian.utils.C.DB_NAME
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule = module {
    //Api
    factory { ApiDataSource(roverService = get(), networkMonitor = get()) }

    //Cache
    single {
        Room.databaseBuilder(
            androidApplication(),
            MartianLocalDatabase::class.java,
            DB_NAME
        )
            .allowMainThreadQueries()
            .fallbackToDestructiveMigration()
            .build()
    }

    //Dao
    single { get<MartianLocalDatabase>().roverInfo() }
    single { get<MartianLocalDatabase>().favourites() }
    single { get<MartianLocalDatabase>().cameras() }
}