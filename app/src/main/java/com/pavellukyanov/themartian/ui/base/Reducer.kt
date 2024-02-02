package com.pavellukyanov.themartian.ui.base

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pavellukyanov.themartian.utils.C.ERROR_BROADCAST_ACTION
import com.pavellukyanov.themartian.utils.C.ERROR_MESSAGE
import com.pavellukyanov.themartian.utils.ext.dispatcher
import com.pavellukyanov.themartian.utils.ext.log
import com.pavellukyanov.themartian.utils.ext.suspendDebugLog
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
import org.koin.core.component.inject

abstract class Reducer<STATE : State, ACTION : Action, EFFECT : Effect>(initState: STATE) : ViewModel(), KoinComponent {
    private val tag = this::class.java.simpleName
    protected val context: Context by inject()
    private val mutex = Mutex()
    private val _state: MutableStateFlow<STATE> = MutableStateFlow(initState)
    private val errorHandler = CoroutineExceptionHandler { context, exception ->
        log.e(exception, "Context: ${context.dispatcher}, Exception: $exception")
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
        suspendDebugLog(tag) { "Reduce -> oldState: ${_state.value} | action: $action" }
    }

    protected suspend fun saveState(newState: STATE) = cpu {
        withLock {
            _state.emit(newState)
            suspendDebugLog(tag) { "SaveState -> newState: $newState" }
        }
    }

    protected suspend fun sendEffect(newEffect: EFFECT) = ui {
        withLock {
            effect.send(newEffect)
            suspendDebugLog(tag) { "SendEffect -> newEffect: $newEffect" }
        }
    }

    protected fun withState(onAction: suspend (currentState: STATE) -> Unit) = cpu {
        onAction(_state.value)
    }

    private fun handledError(error: Throwable) {
        log.w("handledError -> ${error.javaClass.simpleName}")
        context.sendBroadcast(Intent(ERROR_BROADCAST_ACTION).putExtra(ERROR_MESSAGE, error.message.orEmpty()))
    }
}