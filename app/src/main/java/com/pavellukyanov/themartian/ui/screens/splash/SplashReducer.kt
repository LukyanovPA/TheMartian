package com.pavellukyanov.themartian.ui.screens.splash

import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.ui.base.Reducer
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

class SplashReducer(
    private val loadRovers: LoadRovers
) : Reducer<SplashState, SplashAction, SplashEffect>(SplashState()) {
    override suspend fun reduce(oldState: SplashState, action: SplashAction) {
        delay(1.seconds)
        onObserveCacheState()
    }

    private fun onObserveCacheState() = io {
        loadRovers()
            .collect { rovers ->
                if (rovers.isNotEmpty()) sendEffect(SplashEffect(state = true))
            }
    }
}