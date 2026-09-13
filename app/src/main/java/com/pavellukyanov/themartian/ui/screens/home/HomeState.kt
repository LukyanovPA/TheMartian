package com.pavellukyanov.themartian.ui.screens.home

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.Rover
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

data class HomeState(
    override val isLoading: Boolean = true,
    val rovers: List<Rover> = listOf(),
    val thumbnails: Map<String, List<Photo>> = emptyMap(),
    val heroPhoto: Photo? = null,
    val isRefreshing: Boolean = false
) : State()

sealed class HomeAction : Action() {
    data object LoadRovers : HomeAction()
    data object Refresh : HomeAction()
    data class OnRoverClick(val rover: Rover) : HomeAction()
    data class LoadThumbnail(val roverName: String) : HomeAction()
}

sealed class HomeEffect : Effect() {
    data class NavigateToRoverGallery(val roverName: String) : HomeEffect()
}
