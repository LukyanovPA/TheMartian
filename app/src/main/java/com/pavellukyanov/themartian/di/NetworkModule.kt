package com.pavellukyanov.themartian.di

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.pavellukyanov.themartian.BuildConfig
import com.pavellukyanov.themartian.data.api.HttpInterceptor
import com.pavellukyanov.themartian.data.api.RoverService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import timber.log.Timber
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
    singleOf(::HttpInterceptor)

    single {
        val httpInterceptor: HttpInterceptor by inject()

        val okHttpBuilder = OkHttpClient.Builder()
            .apply {
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)
            }

        if (BuildConfig.DEBUG) {
            val httpLoggingInterceptor =
                HttpLoggingInterceptor { message -> Timber.tag("OkHttp").d(message) }
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(httpLoggingInterceptor)
        }

        okHttpBuilder.addInterceptor {
            val request = it.request()
            val url = request.url
                .newBuilder()
                .addQueryParameter("api_key", "f8FYngXOCFmWPVOgmcugDO5JwAsPB238oee4wh6V")
                .build()
            it.proceed(request.newBuilder().url(url).build())
        }

        okHttpBuilder.addInterceptor(httpInterceptor)

        val json = Json {
            coerceInputValues = true
            explicitNulls = false
            ignoreUnknownKeys = true
        }

        Retrofit.Builder()
            .baseUrl("https://api.nasa.gov/mars-photos/api/v1/")
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .client(okHttpBuilder.build())
            .build()
    }

    //Services
    single<RoverService> { get<Retrofit>().create(RoverService::class.java) }
}