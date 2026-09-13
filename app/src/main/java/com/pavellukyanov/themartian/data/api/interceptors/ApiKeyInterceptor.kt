package com.pavellukyanov.themartian.data.api.interceptors

import com.pavellukyanov.themartian.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

private const val API_KEY = "X-API-Key"

private const val RELAY_TOKEN = "X-Relay-Token"

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
            .header(API_KEY, BuildConfig.API_KEY)
            .header(RELAY_TOKEN, BuildConfig.RELAY_TOKEN)
            .header("User-Agent", "TheMartian-Android/2.0")
            .header("Accept", "application/json")
            .build()
        return chain.proceed(requestBuilder)
    }
}
