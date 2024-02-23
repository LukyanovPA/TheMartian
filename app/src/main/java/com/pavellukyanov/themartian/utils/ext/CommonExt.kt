package com.pavellukyanov.themartian.utils.ext

import android.content.Context
import androidx.activity.ComponentActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.pavellukyanov.themartian.ui.base.Reducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/** Logging */
val ComponentActivity.log get() = Timber.tag(this::class.java.simpleName)
val Reducer<*, *, *>.log get() = Timber.tag(this::class.java.simpleName)

fun debug(message: () -> String) {
    Timber.v("SMOTRIM -> ${message()}")
}

val CoroutineContext.dispatcher get() = splitContext(this)

private fun splitContext(coroutineContext: CoroutineContext): String {
    val list = coroutineContext.toString().split(',')
    return "[${list[list.lastIndex].trim()}"
}

/** Coroutines */
suspend fun <T> onIo(action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, action)
suspend fun <T> onCpu(action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default, action)

/** List */
suspend fun <T, R> List<T>.onMap(transform: suspend (T) -> R) = onCpu {
    map { transform(it) }
}

/** Flow */
fun <T, R> Flow<T>.onMap(transform: (T) -> R): Flow<R> =
    map(transform)
        .flowOn(Dispatchers.Default)

/** Context */
fun Context.localBroadcast(): LocalBroadcastManager = LocalBroadcastManager.getInstance(this)