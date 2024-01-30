package com.pavellukyanov.themartian.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.pavellukyanov.themartian.ui.wigets.loading.Loading
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import com.pavellukyanov.themartian.ui.base.State as UiState

@Composable
internal fun Flow<UiState>.asUiState(): androidx.compose.runtime.State<UiState> = collectAsStateWithLifecycle(initialValue = UiState())

@Composable
internal inline fun <reified STATE : UiState> UiState.receiveWithPadding(
    padding: PaddingValues,
    crossinline content: @Composable (STATE) -> Unit
) {
    when (this) {
        is STATE -> Scaffold(
            modifier = Modifier.padding(padding)
        ) { paddingValues ->
            AnimatedVisibility(
                modifier = Modifier.padding(paddingValues),
                visible = this.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Loading()
            }
            AnimatedVisibility(
                modifier = Modifier.padding(paddingValues),
                visible = !this.isLoading,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                content(this@receiveWithPadding)
            }
        }
    }
}

@Composable
internal fun Launch(block: suspend CoroutineScope.() -> Unit) {
    LaunchedEffect(key1 = true, block = block)
}