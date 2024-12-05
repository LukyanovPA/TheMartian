package com.pavellukyanov.themartian.data.api.interceptors

import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.google.gson.JsonSyntaxException
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

private const val TAG = "OkHttp"

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        HttpLoggingInterceptor(ApiLogger()).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }.intercept(chain)

    inner class ApiLogger : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            if (message.startsWith("{") || message.startsWith("[")) {
                try {
                    val prettyPrintJson = GsonBuilder().setPrettyPrinting()
                        .create().toJson(JsonParser().parse(message))

                    prettyPrintJson.lines().forEach { print ->
                        Timber.tag(TAG).w(print)
                    }
                } catch (m: JsonSyntaxException) {
                    Timber.tag(TAG).w(message)
                }
            } else {
                Timber.tag(TAG).w(message)
                return
            }
        }
    }
}