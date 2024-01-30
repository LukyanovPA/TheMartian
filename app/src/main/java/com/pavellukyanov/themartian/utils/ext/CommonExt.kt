package com.pavellukyanov.themartian.utils.ext

import kotlinx.coroutines.CoroutineScope
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

/* *
* Logging
* */

val Any.log get() = Timber.tag(this::class.java.simpleName)

fun debug(message: () -> String) {
    Timber.v("SMOTRIM -> ${message()}")
}

val CoroutineContext.dispatcher get() = splitContext(this)

fun CoroutineScope.suspendDebugLog(tag: String? = null, message: () -> String = { "" }) {
    val t = tag ?: "SMOTRIM"
    Timber.tag(t).w("${coroutineContext.dispatcher} -> ${message()}")
}

private fun splitContext(coroutineContext: CoroutineContext): String {
    val list = coroutineContext.toString().split(',')
    return "[${list[list.lastIndex].trim()}"
}