package com.pavellukyanov.themartian.ui.screens.home

import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.ui.base.Reducer

class HomeReducer(
    private val loadRovers: LoadRovers
) : Reducer<HomeState, HomeAction, HomeEffect>(HomeState()) {
    override suspend fun reduce(oldState: HomeState, action: HomeAction) {
        when (action) {
            is HomeAction.LoadRovers -> onLoadRovers()
        }
    }

    private fun onLoadRovers() = io {
        actionWithState { current ->
            saveState(current.copy(isLoading = false, rovers = loadRovers()))
        }
    }
}