package com.pavellukyanov.themartian

import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

data class MainState(
    override val isLoading: Boolean = false
) : State()

sealed class MainAction : Action() {
    data class Error(val errorMessage: String) : MainAction()
    data object CloseErrorDialog : MainAction()
}

sealed class MainEffect : Effect() {
    data class ShowError(val errorMessage: String) : MainEffect()

    data object CloseErrorDialog : MainEffect()
}