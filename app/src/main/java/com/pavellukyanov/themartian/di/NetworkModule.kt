package com.pavellukyanov.themartian.di

import com.google.gson.GsonBuilder
import com.pavellukyanov.themartian.BuildConfig
import com.pavellukyanov.themartian.data.api.RoverService
import com.pavellukyanov.themartian.data.api.interceptors.ApiKeyInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.HttpInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.ImageRelayInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.LoggingInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.RetryInterceptor
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private val BASE_URL = BuildConfig.BASE_URL

private const val CONNECT_TIMEOUT_SECONDS = 15L
private const val READ_TIMEOUT_SECONDS = 15L
private const val WRITE_TIMEOUT_SECONDS = 15L

private const val CALL_TIMEOUT_SECONDS = 50L

val networkModule = module {
    singleOf(::HttpInterceptor)
    singleOf(::ApiKeyInterceptor)
    singleOf(::ImageRelayInterceptor)
    single { RetryInterceptor() }
    singleOf(::LoggingInterceptor)

    single {
        val httpInterceptor: HttpInterceptor by inject()
        val apiKeyInterceptor: ApiKeyInterceptor by inject()
        val imageRelayInterceptor: ImageRelayInterceptor by inject()
        val retryInterceptor: RetryInterceptor by inject()
        val loggingInterceptor: LoggingInterceptor by inject()

        OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .apply {
                connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)

                addInterceptor(loggingInterceptor)
                addInterceptor(apiKeyInterceptor)
                addInterceptor(imageRelayInterceptor)
                addInterceptor(httpInterceptor)
                addInterceptor(retryInterceptor)
            }
            .build()
    }

    single {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(get<OkHttpClient>())
            .build()
    }

    //Services
    single<RoverService> { get<Retrofit>().create(RoverService::class.java) }
}
