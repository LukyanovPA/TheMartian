package com.pavellukyanov.themartian.utils.ext

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.ui.base.State
import com.pavellukyanov.themartian.ui.wigets.loading.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.receiveAsFlow
import com.pavellukyanov.themartian.ui.base.State as UiState

@Composable
internal fun <STATE : State> Reducer<STATE, *, *>.asState(): androidx.compose.runtime.State<UiState> =
    state.collectAsStateWithLifecycle(initialValue = UiState())

@Composable
internal inline fun <reified STATE : UiState> UiState.receive(
    modifier: Modifier,
    crossinline content: @Composable (currentState: STATE) -> Unit
) {
    when (this) {
        is STATE -> if (isLoading) Loading(modifier = modifier) else content(this)
    }
}

@Composable
internal inline fun <reified STATE : UiState> UiState.receive(
    crossinline content: @Composable (currentState: STATE) -> Unit
) {
    when (this) {
        is STATE -> content(this)
    }
}

@Composable
internal fun Launch(block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(key1 = true, block = block)
}

/** Observe EFFECT */
suspend fun <EFFECT : Effect> Reducer<*, *, EFFECT>.subscribeEffect(onEffect: (effect: EFFECT) -> Unit) {
    effect.receiveAsFlow()
        .collect(onEffect)
}