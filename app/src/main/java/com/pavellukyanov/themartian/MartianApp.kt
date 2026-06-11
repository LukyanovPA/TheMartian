package com.pavellukyanov.themartian

import android.app.Application
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST
import androidx.work.WorkManager
import coil3.ImageLoader
import coil3.disk.DiskCache
import coil3.memory.MemoryCache
import coil3.util.DebugLogger
import okio.Path.Companion.toOkioPath
import com.pavellukyanov.themartian.di.commonModule
import com.pavellukyanov.themartian.di.dataModule
import com.pavellukyanov.themartian.di.domainModule
import com.pavellukyanov.themartian.di.helpersModule
import com.pavellukyanov.themartian.di.networkModule
import com.pavellukyanov.themartian.di.reducerModule
import com.pavellukyanov.themartian.di.utilsModule
import com.pavellukyanov.themartian.utils.C.CACHE_SIZE
import com.pavellukyanov.themartian.utils.C.COMMON
import com.pavellukyanov.themartian.utils.C.DEFAULT_CACHE_SIZE
import com.pavellukyanov.themartian.utils.work.DebugCheckFirstStartWork
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

private const val IMAGE_CACHE = "image_cache"

class MartianApp : Application() {
    lateinit var imageLoader: ImageLoader
        private set

    override fun onCreate() {
        super.onCreate()
        imageLoader = createImageLoader()
        initDi()
        if (BuildConfig.DEBUG) initLogger(); debugCheckFirstStart()
    }

    private fun createImageLoader(): ImageLoader {
        val size = applicationContext.getSharedPreferences(COMMON, MODE_PRIVATE).getFloat(CACHE_SIZE, DEFAULT_CACHE_SIZE).toLong()

        return ImageLoader.Builder(this)
            .memoryCache {
                MemoryCache.Builder()
                    .maxSizePercent(this@MartianApp, 0.25)
                    .build()
            }
            .apply {
                if (size > 0) {
                    diskCache {
                        DiskCache.Builder()
                            .directory(cacheDir.resolve(IMAGE_CACHE).toOkioPath())
                            .maxSizeBytes(size * 1024 * 1024)
                            .build()
                    }
                }
            }
            .logger(DebugLogger())
            .build()
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
            modules(helpersModule)
            modules(utilsModule)
        }
    }

    private fun debugCheckFirstStart() {
        val request = OneTimeWorkRequestBuilder<DebugCheckFirstStartWork>()
            .setExpedited(RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .build()

        WorkManager.getInstance(this)
            .enqueue(request)
    }

    private fun initLogger() {
        Timber.plant(Timber.DebugTree())
    }
}
