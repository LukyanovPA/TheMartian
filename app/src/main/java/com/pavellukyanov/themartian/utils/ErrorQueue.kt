package com.pavellukyanov.themartian.utils

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ErrorQueue {
    private val _onError: MutableStateFlow<UiError> = MutableStateFlow(UiError.NoError)
    val onError: StateFlow<UiError> = _onError.asStateFlow()

    fun add(error: Throwable) {
        when (val state = _onError.value) {
            is UiError.Error -> if (state.error != error) _onError.value = UiError.Error(error)
            is UiError.NoError -> _onError.value = UiError.Error(error)
        }
    }

    fun clear() {
        _onError.value = UiError.NoError
    }
}

sealed class UiError {
    data class Error(val error: Throwable) : UiError()
    data object NoError : UiError()
}