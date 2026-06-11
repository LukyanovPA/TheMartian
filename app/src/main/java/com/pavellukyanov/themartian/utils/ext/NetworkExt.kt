package com.pavellukyanov.themartian.utils.ext

import retrofit2.Response

fun <T> Response<T>.toData(): T =
    when {
        isSuccessful -> body()!!
        code() == 304 -> throw ApiException.NotModifiedException
        else -> throw ApiException.ServerException(message = errorBody()?.string())
    }

fun <T> Response<T>.toDataOrNull(): T? =
    if (isSuccessful) body()
    else null

sealed class ApiException(message: String? = null) : Exception(message) {
    class ServerException(message: String? = null) : ApiException(message)
    class ClientException(message: String?) : ApiException(message)
    class ConnectionException(message: String?) : ApiException(message)
    class UndefinedException(message: String?) : ApiException(message)
    data object NotModifiedException : ApiException("Not Modified")
}
