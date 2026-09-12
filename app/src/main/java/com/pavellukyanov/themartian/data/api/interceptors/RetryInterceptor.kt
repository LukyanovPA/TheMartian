package com.pavellukyanov.themartian.data.api.interceptors

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.math.min
import kotlin.random.Random

private const val TAG = "RetryInterceptor"

private const val DEFAULT_MAX_RETRIES = 2
private const val DEFAULT_INITIAL_BACKOFF_MS = 400L
private const val DEFAULT_MAX_BACKOFF_MS = 2_000L

class RetryInterceptor(
    private val maxRetries: Int = DEFAULT_MAX_RETRIES,
    private val initialBackoffMs: Long = DEFAULT_INITIAL_BACKOFF_MS,
    private val maxBackoffMs: Long = DEFAULT_MAX_BACKOFF_MS
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val isIdempotent = request.method == "GET" || request.method == "HEAD"

        var attempt = 0
        while (true) {
            try {
                val response = chain.proceed(request)

                if (!isIdempotent || attempt >= maxRetries || !response.isTransient()) return response

                Timber.tag(TAG).w(
                    "%s %s -> HTTP %d, retry %d of %d",
                    request.method, request.url, response.code, attempt + 1, maxRetries
                )
                response.close()
            } catch (e: IOException) {
                if (chain.call().isCanceled() ||
                    !isIdempotent ||
                    attempt >= maxRetries ||
                    !e.isTransient()
                ) throw e

                Timber.tag(TAG).w(
                    e,
                    "%s %s failed, retry %d of %d",
                    request.method, request.url, attempt + 1, maxRetries
                )
            }

            sleepBeforeRetry(attempt)
            attempt++
        }
    }

    private fun Response.isTransient(): Boolean = code in 500..599

    private fun IOException.isTransient(): Boolean = when (this) {
        is SocketTimeoutException -> true
is SocketException -> true
        is UnknownHostException -> true
        else -> false
    }

    private fun sleepBeforeRetry(attempt: Int) {
        val backoffMs = min(initialBackoffMs shl attempt, maxBackoffMs)
        val delayMs = backoffMs + Random.nextLong(backoffMs / 2 + 1)
        try {
            Thread.sleep(delayMs)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw InterruptedIOException("Retry interrupted").apply { initCause(e) }
        }
    }
}
