package com.pavellukyanov.themartian

import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State
import com.pavellukyanov.themartian.utils.C.LONG_ZERO

data class MainState(
    override val isLoading: Boolean = false,
    val imageCacheSize: Long = LONG_ZERO,
    val databaseSize: Long = LONG_ZERO
) : State()

sealed class MainAction : Action() {
    data class Error(val error: Throwable) : MainAction()
    data object CloseErrorDialog : MainAction()
}

sealed class MainEffect : Effect() {
    data class ShowError(val errorMessage: String) : MainEffect()

    data object CloseErrorDialog : MainEffect()
}