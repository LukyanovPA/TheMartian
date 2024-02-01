package com.pavellukyanov.themartian.ui.screens.home

import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.ui.base.Reducer

class HomeReducer(
    private val loadRovers: LoadRovers
) : Reducer<HomeState, HomeAction, HomeEffect>(HomeState()) {
    override suspend fun reduce(oldState: HomeState, action: HomeAction) {
        when (action) {
            is HomeAction.LoadRovers -> onLoadRovers()
            is HomeAction.OnRoverClick -> sendEffect(HomeEffect.NavigateToRoverGallery(roverName = action.rover.roverName))
        }
    }

    private fun onLoadRovers() = io {
        loadRovers()
            .collect { rovers ->
                actionWithState { current ->
                    saveState(current.copy(isLoading = rovers.isEmpty(), rovers = rovers))
                }
            }
    }
}