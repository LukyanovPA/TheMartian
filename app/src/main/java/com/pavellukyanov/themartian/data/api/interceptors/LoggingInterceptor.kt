package com.pavellukyanov.themartian.data.api.interceptors

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

private const val TAG = "OkHttp"

private val prettyPrintingGson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .setLenient()
    .create()

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        HttpLoggingInterceptor(ApiLogger()).apply {
            level = HttpLoggingInterceptor.Level.BODY
        }.intercept(chain)

    private class ApiLogger : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {
            if (message.startsWith("{") || message.startsWith("[")) {
                val prettyPrinted = try {
                    prettyPrintingGson.toJson(JsonParser.parseString(message))
                } catch (_: Throwable) {
                    message
                }
                prettyPrinted.lineSequence().forEach { Timber.tag(TAG).w(it) }
            } else {
                Timber.tag(TAG).w(message)
            }
        }
    }
}
