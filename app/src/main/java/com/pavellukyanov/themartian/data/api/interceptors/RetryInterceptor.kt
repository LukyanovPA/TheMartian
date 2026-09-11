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

/**
 * Retries idempotent requests that failed with a transient network error — a stalled
 * socket ([SocketTimeoutException]), a dropped connection or a DNS hiccup — as well as
 * those answered with a server-side 5xx.
 *
 * The API answers in well under a second, so a failure like this is a hiccup rather than
 * an outage; retrying it keeps the screen from going empty on a single bad packet.
 *
 * Retries are limited to `GET`/`HEAD` on purpose: repeating a request that changes state
 * on the server is not safe. The whole thing is additionally bounded by the client's
 * `callTimeout`, so retries can never stretch a request out indefinitely.
 */
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
                // The failed response holds a connection, so it has to be released
                // before the next attempt can run.
                response.close()
            } catch (e: IOException) {
                // A cancelled call means the caller has walked away — the screen closed, the
                // coroutine was dropped. Whatever the socket reported on the way down, firing
                // the request again would be a call nobody is waiting for.
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

    /** A 5xx is the server asking to be tried again; a 4xx is the caller's own fault. */
    private fun Response.isTransient(): Boolean = code in 500..599

    /**
     * Only the errors a fresh connection can plausibly fix. A plain [InterruptedIOException]
     * is deliberately absent: that is what OkHttp raises once the call's own timeout budget
     * is spent or the thread was interrupted, and there is nothing left to retry into.
     */
    private fun IOException.isTransient(): Boolean = when (this) {
        is SocketTimeoutException -> true
        is SocketException -> true // covers ConnectException, connection reset, broken pipe
        is UnknownHostException -> true
        else -> false
    }

    private fun sleepBeforeRetry(attempt: Int) {
        val backoffMs = min(initialBackoffMs shl attempt, maxBackoffMs)
        // Jitter keeps requests that failed together from retrying in lockstep.
        val delayMs = backoffMs + Random.nextLong(backoffMs / 2 + 1)
        try {
            Thread.sleep(delayMs)
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            throw InterruptedIOException("Retry interrupted").apply { initCause(e) }
        }
    }
}
