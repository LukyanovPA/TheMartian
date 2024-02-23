package com.pavellukyanov.themartian

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import com.pavellukyanov.themartian.di.commonModule
import com.pavellukyanov.themartian.di.dataModule
import com.pavellukyanov.themartian.di.domainModule
import com.pavellukyanov.themartian.di.networkModule
import com.pavellukyanov.themartian.di.reducerModule
import com.pavellukyanov.themartian.utils.C.CACHE_SIZE
import com.pavellukyanov.themartian.utils.C.COMMON
import com.pavellukyanov.themartian.utils.C.DB_NAME
import com.pavellukyanov.themartian.utils.C.DEFAULT_CACHE_SIZE
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

private const val FIRST_START_KEY = "FIRST_START_KEY"
private const val IMAGE_CACHE = "image_cache"

class MartianApp : Application(), ImageLoaderFactory {
    override fun onCreate() {
        super.onCreate()
        initDi()
        initLogger()
        if (BuildConfig.DEBUG) checkFirstStart()

//        this.applicationContext.deleteDatabase("MartianLocalDatabase.db")
    }

    private fun initDi() {
        startKoin {
            androidLogger()
            androidContext(this@MartianApp)

            //Modules
            modules(networkModule)
            modules(dataModule)
            modules(domainModule)
            modules(reducerModule)
            modules(commonModule)
        }
    }

    private fun checkFirstStart() {
        try {
            val preferences = applicationContext.getSharedPreferences(COMMON, MODE_PRIVATE)
            val result = preferences.getBoolean(FIRST_START_KEY, false)
            if (!result) {
                if (applicationContext.getDatabasePath(DB_NAME).canRead())
                    applicationContext.deleteDatabase(DB_NAME)

                preferences.edit().putBoolean(FIRST_START_KEY, true).apply()
            }
        } catch (e: Throwable) {
            Timber.e(e)
        }
    }

    private fun initLogger() {
        if (BuildConfig.DEBUG) Timber.plant(Timber.DebugTree())
    }

    override fun newImageLoader(): ImageLoader =
        ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .apply {
                val size = applicationContext.getSharedPreferences(COMMON, MODE_PRIVATE).getFloat(CACHE_SIZE, DEFAULT_CACHE_SIZE).toLong()

                if (size > 0) {
                    diskCache {
                        DiskCache.Builder()
                            .directory(cacheDir.resolve(IMAGE_CACHE))
                            .maxSizeBytes(size * 1024 * 1024)
                            .build()
                    }
                }
            }
            .logger(DebugLogger())
            .respectCacheHeaders(true)
            .build()
}