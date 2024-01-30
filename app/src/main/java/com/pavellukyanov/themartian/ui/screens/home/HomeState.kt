package com.pavellukyanov.themartian.ui.screens.home

import com.pavellukyanov.themartian.domain.entity.Rover
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

data class HomeState(
    override val isLoading: Boolean = true,
    val rovers: List<Rover> = listOf()
) : State()

sealed class HomeAction : Action() {
    data object LoadRovers : HomeAction()
}

sealed class HomeEffect : Effect()