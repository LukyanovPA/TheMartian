package com.pavellukyanov.themartian.data.api.interceptors

import com.pavellukyanov.themartian.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

private const val API_KEY = "api_key"

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url
            .newBuilder()
            .addQueryParameter(API_KEY, BuildConfig.API_KEY)
            .build()
        val requestBuilder = request.newBuilder()
            .url(url)
            .header("Connection", "close")
            .build()
        return chain.proceed(requestBuilder)
    }
}