package com.pavellukyanov.themartian.utils.ext

import timber.log.Timber

val Any.log get() = Timber.tag(this::class.java.simpleName)

fun debug(message: () -> String) {
    Timber.d("SMOTRIM -> ${message()}")
}