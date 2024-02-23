package com.pavellukyanov.themartian.di

import com.google.gson.GsonBuilder
import com.pavellukyanov.themartian.BuildConfig
import com.pavellukyanov.themartian.data.api.HttpInterceptor
import com.pavellukyanov.themartian.data.api.RoverService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.nasa.gov/mars-photos/api/v1/"
private const val API_KEY = "api_key"
private const val TAG = "OkHttp"

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
                HttpLoggingInterceptor { message -> Timber.tag(TAG).d(message) }
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            okHttpBuilder.addInterceptor(httpLoggingInterceptor)
        }

        okHttpBuilder.addInterceptor {
            val request = it.request()
            val url = request.url
                .newBuilder()
                .addQueryParameter(API_KEY, BuildConfig.API_KEY)
                .build()
            it.proceed(request.newBuilder().url(url).build())
        }

        okHttpBuilder.addInterceptor(httpInterceptor)

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpBuilder.build())
            .build()
    }

    //Services
    single<RoverService> { get<Retrofit>().create(RoverService::class.java) }
}