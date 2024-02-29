package com.pavellukyanov.themartian.ui.base

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavellukyanov.themartian.data.api.ApiException
import com.pavellukyanov.themartian.utils.C.ERROR
import com.pavellukyanov.themartian.utils.C.ERROR_BROADCAST_ACTION
import com.pavellukyanov.themartian.utils.ext.dispatcher
import com.pavellukyanov.themartian.utils.ext.localBroadcast
import com.pavellukyanov.themartian.utils.ext.log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

abstract class Reducer<STATE : State, ACTION : Action, EFFECT : Effect>(initState: STATE) : ViewModel(), KoinComponent {
    protected val context: Context by inject()
    protected val _state: MutableStateFlow<STATE> = MutableStateFlow(initState)
    private val errorHandler = CoroutineExceptionHandler { context, exception ->
        log.e(exception, "Context: ${context.dispatcher}, Exception: $exception")
        if (exception !is ApiException.UndefinedException) handledError(exception)
    }

    val state: StateFlow<STATE> = _state.asStateFlow()
    val effect: Channel<EFFECT> = Channel()

    abstract suspend fun reduce(oldState: STATE, action: ACTION)

    protected fun ui(action: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.Main, action)

    protected fun io(action: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.IO, action)

    protected fun cpu(action: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.Default, action)

    private fun launch(dispatcher: CoroutineDispatcher, action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(context = dispatcher + errorHandler, block = action)
    }

    fun sendAction(action: ACTION) = cpu {
        reduce(_state.value, action)
        log.d("Reduce -> oldState: ${_state.value} | action: $action")
    }

    protected fun saveState(newState: STATE) {
        _state.value = newState
    }

    protected suspend fun sendEffect(newEffect: EFFECT) = ui {
        effect.send(newEffect)
        log.d("SendEffect -> newEffect: $newEffect")
    }

    protected fun withState(onAction: suspend CoroutineScope.(currentState: STATE) -> Unit) = cpu {
        onAction(_state.value)
    }

    protected fun handledError(error: Throwable) {
        log.w("handledError -> ${error.javaClass.simpleName}")
        context.localBroadcast().sendBroadcast(Intent(ERROR_BROADCAST_ACTION).putExtra(ERROR, error))
    }
}