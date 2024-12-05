package com.pavellukyanov.themartian.data.api.interceptors

import okhttp3.Interceptor
import okhttp3.Response

class ContentLengthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request())
            .newBuilder()
            .removeHeader("Content-Length")
            .build()
}