package com.pavellukyanov.themartian.data.dto

import retrofit2.Response

fun <T> Response<T>.toData(): T =
    if (isSuccessful) {
        body()!!
    } else {
        throw ApiException(exceptionCode = code(), message = errorBody()?.string())
    }

class ApiException(val exceptionCode: Int, message: String? = null) : Exception(message)