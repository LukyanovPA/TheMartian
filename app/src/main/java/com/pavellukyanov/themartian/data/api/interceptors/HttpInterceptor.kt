package com.pavellukyanov.themartian.data.api.interceptors

import com.pavellukyanov.themartian.utils.ext.ApiException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Turns statuses and I/O errors into [ApiException] so the rest of the app deals with one
 * exception family.
 *
 * Every branch here has to leave through [ApiException] — see the note on that class. An
 * interceptor that throws anything else kills the process on the OkHttp dispatcher thread.
 */
class HttpInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        try {
            safeHandleResponse(chain)
        } catch (apiException: ApiException) {
            throw apiException
        } catch (e: Exception) {
            Timber.e(e)
            if (e is ConnectException || e is UnknownHostException || e is SocketTimeoutException) {
                throw ApiException.ConnectionException(message = e.message)
            }
            throw ApiException.UndefinedException(e.message)
        }

    private fun safeHandleResponse(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code in HttpResponseCode.OK.errorCode ||
            response.code in HttpResponseCode.NOT_MODIFIED.errorCode
        ) return response

        // Every branch below leaves by throwing, so nobody will ever read this body. Closing
        // it first hands the connection back to the pool instead of stranding it.
        val code = response.code
        val message = response.message
        response.close()

        throw when (code) {
            in HttpResponseCode.NOT_FOUND.errorCode -> ApiException.ClientException("Resource not found")
            in HttpResponseCode.VALIDATION_ERROR.errorCode -> ApiException.ClientException("Validation error")
            in HttpResponseCode.SERVER_ERROR.errorCode -> ApiException.ServerException(message = message)
            in HttpResponseCode.MANY_REQUESTS.errorCode -> ApiException.ServerException(message = message)
            in HttpResponseCode.BAD_REQUEST.errorCode -> ApiException.ClientException(message = message)
            // Any other status — a redirect, 401, 403, 405, an unknown 5xx. Reporting it as an
            // ordinary API error keeps it from becoming a process-killing throwable.
            else -> ApiException.UndefinedException(message = "Unexpected response with code: $code")
        }
    }
}

private enum class HttpResponseCode(val errorCode: IntRange) {
    OK(200..299),
    NOT_MODIFIED(304..304),
    BAD_REQUEST(400..400),
    NOT_FOUND(404..404),
    VALIDATION_ERROR(422..422),
    MANY_REQUESTS(429..429),
    SERVER_ERROR(500..526)
}
