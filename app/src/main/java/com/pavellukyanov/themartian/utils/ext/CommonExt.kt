package com.pavellukyanov.themartian.utils.ext

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.lifecycle.LifecycleService
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
val LifecycleService.log get() = Timber.tag(this::class.java.simpleName)
val Reducer<*, *, *>.log get() = Timber.tag(this::class.java.simpleName)

fun debug(message: () -> String) {
    Timber.v("SMOTRIM -> ${message()}")
}

val CoroutineContext.dispatcher get() = splitContext(this)

fun CoroutineScope.suspendDebugLog(tag: String? = null, message: () -> String = { "" }) {
    Timber.tag(tag ?: "SMOTRIM").w("${coroutineContext.dispatcher} -> ${message()}")
}

private fun splitContext(coroutineContext: CoroutineContext): String {
    val list = coroutineContext.toString().split(',')
    return "[${list[list.lastIndex].trim()}"
}

/** Check current SDK */
fun checkSdkVersion(less33: () -> Unit, more33: () -> Unit) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) less33() else more33()
}

/** Coroutines */
suspend fun <T> onIo(action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.IO, action)
suspend fun <T> onCpu(action: suspend CoroutineScope.() -> T) = withContext(Dispatchers.Default, action)

/** List */
suspend fun <T, R> List<T>.onMap(transform: suspend (T) -> R) = onCpu {
    map { transform(it) }
}

/** Flow<List> */
fun <T, R> Flow<List<T>>.onMap(transform: (List<T>) -> List<R>): Flow<List<R>> =
    map(transform)
        .flowOn(Dispatchers.Default)