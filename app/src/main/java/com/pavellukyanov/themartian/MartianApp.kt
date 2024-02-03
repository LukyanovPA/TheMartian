package com.pavellukyanov.themartian

import android.app.Application
import android.content.pm.ApplicationInfo
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.pavellukyanov.themartian.di.dataModule
import com.pavellukyanov.themartian.di.domainModule
import com.pavellukyanov.themartian.di.networkModule
import com.pavellukyanov.themartian.di.reducerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MartianApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        initLogger()

        startKoin {
            androidLogger()
            androidContext(this@MartianApp)

            //Modules
            modules(networkModule)
            modules(dataModule)
            modules(domainModule)
            modules(reducerModule)
        }

//        this.applicationContext.deleteDatabase("MartianLocalDatabase.db")
    }

    private fun initLogger() {
        if ((this.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.20)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(5 * 1024 * 1024)
                    .build()
            }
            .logger(DebugLogger())
            .respectCacheHeaders(false)
            .build()
}