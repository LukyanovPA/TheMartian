package com.pavellukyanov.themartian.utils

import kotlinx.coroutines.flow.MutableStateFlow

class ErrorQueue {
    val onError: MutableStateFlow<UiError> = MutableStateFlow(UiError.NoError)

    fun add(error: Throwable) {
        when (val state = onError.value) {
            is UiError.Error -> if (state.error != error) onError.value = UiError.Error(error)
            is UiError.NoError -> onError.value = UiError.Error(error)
        }
    }

    fun clear() {
        onError.value = UiError.NoError
    }
}

sealed class UiError {
    data class Error(val error: Throwable) : UiError()
    data object NoError : UiError()
}