package com.pavellukyanov.themartian

import com.pavellukyanov.themartian.ui.base.Reducer

class MainActivityReducer : Reducer<MainState, MainAction, MainEffect>(MainState()) {
    override suspend fun reduce(oldState: MainState, action: MainAction) {
        when (action) {
            is MainAction.Error -> sendEffect(MainEffect.ShowError(errorMessage = action.errorMessage))
            is MainAction.CloseErrorDialog -> sendEffect(MainEffect.CloseErrorDialog)
        }
    }
}