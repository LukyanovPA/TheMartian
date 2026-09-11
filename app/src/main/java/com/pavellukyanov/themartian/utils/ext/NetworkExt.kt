package com.pavellukyanov.themartian.utils.ext

import retrofit2.Response
import java.io.IOException

fun <T> Response<T>.toData(): T =
    when {
        isSuccessful -> body()!!
        code() == 304 -> throw ApiException.NotModifiedException
        else -> throw ApiException.ServerException(message = errorBody()?.string())
    }

/**
 * This has to stay an [IOException], not a plain [Exception].
 *
 * [com.pavellukyanov.themartian.data.api.interceptors.HttpInterceptor] throws these out of
 * `Interceptor.intercept`, and that is the only kind of throwable an interceptor is allowed
 * to raise. OkHttp's `RealCall.AsyncCall.run` catches [IOException] and hands it to the
 * callback, but any other `Throwable` is passed on *and then rethrown* — on the dispatcher
 * thread, where nothing catches it, so it takes the whole process down with
 * `FATAL EXCEPTION: OkHttp Dispatcher`. Since every endpoint here is a Retrofit `suspend`
 * function, every response goes through that async path: as a plain `Exception` a single 5xx
 * or a connection drop was enough to kill the app.
 */
sealed class ApiException(message: String? = null) : IOException(message) {
    class ServerException(message: String? = null) : ApiException(message)
    class ClientException(message: String?) : ApiException(message)
    class ConnectionException(message: String?) : ApiException(message)
    class UndefinedException(message: String?) : ApiException(message)
    data object NotModifiedException : ApiException("Not Modified")
}
