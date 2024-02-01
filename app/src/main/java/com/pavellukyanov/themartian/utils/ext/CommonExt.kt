package com.pavellukyanov.themartian.utils.ext

import android.os.Build
import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/** Logging */
val Any.log get() = Timber.tag(this::class.java.simpleName)

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
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
        less33()
    } else {
        more33()
    }
}