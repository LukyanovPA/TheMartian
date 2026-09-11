package com.pavellukyanov.themartian.di

import com.google.gson.GsonBuilder
import com.pavellukyanov.themartian.data.api.RoverService
import com.pavellukyanov.themartian.data.api.interceptors.ApiKeyInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.HttpInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.LoggingInterceptor
import com.pavellukyanov.themartian.data.api.interceptors.RetryInterceptor
import okhttp3.OkHttpClient
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = "https://api.marsvista.dev/api/v2/"

// The API answers in well under a second, so these are generous. Using 60s to avoid
// read timeouts on slow connections or when the server is under load.
private const val CONNECT_TIMEOUT_SECONDS = 60L
private const val READ_TIMEOUT_SECONDS = 60L
private const val WRITE_TIMEOUT_SECONDS = 60L

// Ceiling for a whole call, retries included, so a request can never hang forever.
// It has to stay above (maxRetries + 1) * READ_TIMEOUT_SECONDS plus the backoff waits —
// 3 * 60s + ~1.5s — or the call timeout cuts the last attempt short and the retry never
// gets to finish on its own.
private const val CALL_TIMEOUT_SECONDS = 200L

val networkModule = module {
    singleOf(::HttpInterceptor)
    singleOf(::ApiKeyInterceptor)
    // Not singleOf: that resolves every constructor parameter from the container, and the
    // tuning knobs are plain Int/Long with defaults rather than injectable types.
    single { RetryInterceptor() }
    singleOf(::LoggingInterceptor)

    single {
        val httpInterceptor: HttpInterceptor by inject()
        val apiKeyInterceptor: ApiKeyInterceptor by inject()
        val retryInterceptor: RetryInterceptor by inject()
        val loggingInterceptor: LoggingInterceptor by inject()

        val okHttpClient = OkHttpClient.Builder()
            .retryOnConnectionFailure(true)
            .apply {
                connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                callTimeout(CALL_TIMEOUT_SECONDS, TimeUnit.SECONDS)

                addInterceptor(loggingInterceptor)
                addInterceptor(apiKeyInterceptor)
                addInterceptor(httpInterceptor)
                // Added last, right above the network, so it sees the raw status codes
                // and I/O errors before HttpInterceptor turns them into ApiException.
                addInterceptor(retryInterceptor)
            }
            .build()

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().setLenient().create()))
            .client(okHttpClient)
            .build()
    }

    //Services
    single<RoverService> { get<Retrofit>().create(RoverService::class.java) }
}