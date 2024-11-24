package com.pavellukyanov.themartian.data.api.interceptors

import com.pavellukyanov.themartian.data.api.ApiException
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

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
        val initialResponse = chain.proceed(chain.request())

        return when (initialResponse.code) {
            in HttpResponseCode.OK.errorCode -> initialResponse
            in HttpResponseCode.SERVER_ERROR.errorCode -> throw ApiException.ServerException(message = initialResponse.message)
            in HttpResponseCode.MANY_REQUESTS.errorCode -> throw ApiException.ServerException(message = initialResponse.message)
            in HttpResponseCode.BAD_REQUEST.errorCode -> throw ApiException.ClientException(message = initialResponse.message)
            else -> throw IllegalStateException("Unexpected response with code: ${initialResponse.code} and body: ${initialResponse.body}")
        }
    }
}

private enum class HttpResponseCode(val errorCode: IntRange) {
    OK(200..299),
    BAD_REQUEST(400..400),
    MANY_REQUESTS(429..429),
    SERVER_ERROR(500..526)
}