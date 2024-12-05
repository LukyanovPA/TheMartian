package com.pavellukyanov.themartian.data.api

import retrofit2.Response

fun <T> Response<T>.toData(): T =
    if (isSuccessful) body()!!
    else throw ApiException.ServerException(message = errorBody()?.string())

sealed class ApiException(message: String? = null) : Exception(message) {
    class ServerException(message: String? = null) : ApiException(message)
    class ClientException(message: String?) : ApiException(message)
    class ConnectionException(message: String?) : ApiException(message)
    class UndefinedException(message: String?) : ApiException(message)
}