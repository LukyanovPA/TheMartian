package com.pavellukyanov.themartian.di

import com.google.gson.GsonBuilder
import com.pavellukyanov.themartian.BuildConfig
import com.pavellukyanov.themartian.data.api.RoverService
import com.pavellukyanov.themartian.data.api.interceptors.ApiKeyInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.ContentLengthInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.HttpInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.LoggingInterceptor
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.nasa.gov/mars-photos/api/v1/"

val networkModule = module {
    singleOf(::HttpInterceptor)
    singleOf(::LoggingInterceptor)
    singleOf(::ApiKeyInterceptor)
    singleOf(::ContentLengthInterceptor)

    single {
        val httpInterceptor: HttpInterceptor by inject()
        val loggingInterceptor: LoggingInterceptor by inject()
        val apiKeyInterceptor: ApiKeyInterceptor by inject()
        val contentLengthInterceptor: ContentLengthInterceptor by inject()

        val okHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .apply {
                connectTimeout(30, TimeUnit.SECONDS)
                readTimeout(30, TimeUnit.SECONDS)
                writeTimeout(30, TimeUnit.SECONDS)

                addInterceptor(contentLengthInterceptor)
                addInterceptor(apiKeyInterceptor)
                addInterceptor(httpInterceptor)
                if (BuildConfig.DEBUG) addInterceptor(loggingInterceptor)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(okHttpClient)
            .build()
    }

    //Services
    single<RoverService> { get<Retrofit>().create(RoverService::class.java) }
}