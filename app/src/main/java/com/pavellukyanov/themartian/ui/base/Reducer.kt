package com.pavellukyanov.themartian.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.koin.core.component.KoinComponent

abstract class Reducer<STATE : State, ACTION : Action, EFFECT : Effect>(initState: STATE) : ViewModel(), KoinComponent {
    private val mutex = Mutex()
    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initState)
    private val errorHandler = CoroutineExceptionHandler { context, exception ->
        log.e(exception, "Context: $context, Exception: $exception")
        handledError(exception)
    }

    val state: StateFlow<STATE> = _state.asStateFlow()
    val effect: Channel<EFFECT> = Channel()

    abstract suspend fun reduce(oldState: STATE, action: ACTION)

    protected fun ui(action: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.Main, action)

    protected fun io(action: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.IO, action)

    protected fun cpu(action: suspend CoroutineScope.() -> Unit) = launch(Dispatchers.Default, action)

    protected suspend fun <T> withLock(action: suspend () -> T) = mutex.withLock { action() }

    private fun launch(dispatcher: CoroutineDispatcher, action: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(context = dispatcher + errorHandler, block = action)
    }

    fun sendAction(action: ACTION) = cpu {
        reduce(_state.value, action)
        log.v("Reduce -> oldState: ${_state.value} | action: $action")
    }

    protected suspend fun saveState(newState: STATE) = cpu {
        withLock {
            _state.emit(newState)
            log.v("SaveState -> newState: $newState")
        }
    }

    protected suspend fun sendEffect(newEffect: EFFECT) = ui {
        withLock {
            effect.send(newEffect)
            log.v("SendEffect -> newEffect: $newEffect")
        }
    }

    protected fun actionWithState(onAction: suspend (STATE) -> Unit) = cpu {
        onAction(_state.value)
    }

    protected fun handledError(error: Throwable) {

    }
}