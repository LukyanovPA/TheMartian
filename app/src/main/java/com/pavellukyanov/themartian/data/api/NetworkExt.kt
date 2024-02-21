package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.utils.ext.debug
import okhttp3.Interceptor
import retrofit2.Response
import timber.log.Timber
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun <T> Response<T>.toData(): T =
    if (isSuccessful) {
        body()!!
    } else {
        throw ApiException.ServerException(message = errorBody()?.string())
    }

sealed class ApiException(message: String? = null) : Exception(message) {
    class ServerException(message: String? = null) : ApiException(message)
    class ClientException(message: String?) : ApiException(message)
    class ConnectionException(message: String?) : ApiException(message)
    class UndefinedException(throwable: Throwable) : ApiException(throwable.message) {
        init {
            addSuppressed(throwable)
        }
    }
}

class HttpInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): okhttp3.Response =
        try {
            safeHandleResponse(chain)
        } catch (apiException: ApiException) {
            throw apiException
        } catch (e: Exception) {
            Timber.e(e)
            if (e is ConnectException || e is UnknownHostException || e is SocketTimeoutException) {
                throw ApiException.ConnectionException(message = e.message)
            }
            throw ApiException.UndefinedException(e)
        }

    private fun safeHandleResponse(chain: Interceptor.Chain): okhttp3.Response {
        val initialResponse = chain.proceed(chain.request())

        return when (initialResponse.code) {
            in HttpResponseCode.OK.errorCode -> initialResponse
            in HttpResponseCode.SERVER_ERROR.errorCode -> throw ApiException.ServerException(message = initialResponse.message)
            in HttpResponseCode.BAD_REQUEST.errorCode -> throw ApiException.ClientException(message = initialResponse.message)
            else -> throw IllegalStateException("Unexpected response with code: ${initialResponse.code} and body: ${initialResponse.body}")
        }
    }
}

private enum class HttpResponseCode(val errorCode: IntRange) {
    OK(200..299),
    BAD_REQUEST(400..400),
    SERVER_ERROR(500..526)
}