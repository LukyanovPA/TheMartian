package com.pavellukyanov.themartian.data.api.interceptors

import com.pavellukyanov.themartian.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

private const val API_KEY = "X-API-Key"

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
            .header(API_KEY, BuildConfig.API_KEY)
            .header("Connection", "close")
            .build()
        return chain.proceed(requestBuilder)
    }
}