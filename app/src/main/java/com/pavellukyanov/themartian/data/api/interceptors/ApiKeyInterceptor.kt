package com.pavellukyanov.themartian.data.api.interceptors

import com.pavellukyanov.themartian.BuildConfig
import okhttp3.Interceptor
import okhttp3.Response

private const val API_KEY = "X-API-Key"

class ApiKeyInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        // Accept-Encoding намеренно не выставляется: OkHttp сам добавляет `gzip` и прозрачно
        // распаковывает ответ — но только если приложение не задало заголовок само. Ручной
        // `Accept-Encoding: gzip` отключает распаковку, и в Gson прилетают сырые gzip-байты
        // (JsonSyntaxException: Expected BEGIN_OBJECT but was STRING), хотя лог OkHttp при этом
        // показывает читаемый JSON — он распаковывает копию буфера только для вывода.
        val requestBuilder = request.newBuilder()
            .header(API_KEY, BuildConfig.API_KEY)
            .header("User-Agent", "TheMartian-Android/2.0")
            .header("Accept", "application/json")
            .build()
        return chain.proceed(requestBuilder)
    }
}