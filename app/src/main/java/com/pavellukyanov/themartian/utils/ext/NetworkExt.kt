package com.pavellukyanov.themartian.utils.ext

import retrofit2.Response
import java.io.IOException

fun <T> Response<T>.toData(): T =
    when {
        isSuccessful -> body()!!
        code() == 304 -> throw ApiException.NotModifiedException
        else -> throw ApiException.ServerException(message = errorBody()?.string())
    }

sealed class ApiException(message: String? = null) : IOException(message) {
    class ServerException(message: String? = null) : ApiException(message)
    class ClientException(message: String?) : ApiException(message)
    class ConnectionException(message: String?) : ApiException(message)
    class UndefinedException(message: String?) : ApiException(message)
    data object NotModifiedException : ApiException("Not Modified")
}
