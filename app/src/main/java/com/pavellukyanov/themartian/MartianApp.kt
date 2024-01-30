package com.pavellukyanov.themartian

import android.app.Application
import android.content.pm.ApplicationInfo
import com.pavellukyanov.themartian.di.dataModule
import com.pavellukyanov.themartian.di.domainModule
import com.pavellukyanov.themartian.di.networkModule
import com.pavellukyanov.themartian.di.reducerModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MartianApp : Application() {
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
    }

    private fun initLogger() {
        if ((this.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0) Timber.plant(Timber.DebugTree())
    }
}