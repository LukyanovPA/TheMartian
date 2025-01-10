package com.pavellukyanov.themartian.ui.screens.splash

import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.ui.base.Reducer

class SplashReducer(
    private val loadRovers: LoadRovers
) : Reducer<SplashState, SplashAction, SplashEffect>(SplashState()) {
    override suspend fun reduce(oldState: SplashState, action: SplashAction) {
        loadRovers()
            .collect { rovers ->
                sendEffect(SplashEffect(state = rovers.isNotEmpty()))
            }
    }
}