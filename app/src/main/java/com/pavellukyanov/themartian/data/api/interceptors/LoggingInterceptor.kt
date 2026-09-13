package com.pavellukyanov.themartian.data.api.interceptors

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber

private const val TAG = "OkHttp"

private val prettyPrintingGson: Gson = GsonBuilder()
    .setPrettyPrinting()
    .setLenient()
    .create()

private val IMAGE_EXTENSIONS = listOf("jpg", "jpeg", "png", "webp", "gif")

private fun Request.isImage(): Boolean =
    url.encodedPath.substringAfterLast('.', missingDelimiterValue = "").lowercase() in IMAGE_EXTENSIONS

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return HttpLoggingInterceptor(ApiLogger())
            .apply {
                level = if (request.isImage()) {
                    HttpLoggingInterceptor.Level.BASIC
                } else {
                    HttpLoggingInterceptor.Level.BODY
                }
                redactHeader("X-API-Key")
                redactHeader("X-Relay-Token")
            }
            .intercept(chain)
    }

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
