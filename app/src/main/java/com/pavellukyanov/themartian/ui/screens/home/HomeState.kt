package com.pavellukyanov.themartian.ui.screens.home

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.Rover
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

data class HomeState(
    override val isLoading: Boolean = true,
    val rovers: List<Rover> = listOf(),
    // Purely decorative — fetched best-effort, one request per rover, after the list is
    // already showing. A rover missing here just means its card renders without a preview
    // strip; it never blocks the screen or surfaces an error (see HomeReducer.onLoadThumbnail).
    val thumbnails: Map<String, List<Photo>> = emptyMap()
) : State()

sealed class HomeAction : Action() {
    data object LoadRovers : HomeAction()
    data class OnRoverClick(val rover: Rover) : HomeAction()
    data class LoadThumbnail(val roverName: String) : HomeAction()
}

sealed class HomeEffect : Effect() {
    data class NavigateToRoverGallery(val roverName: String) : HomeEffect()
    data object ShowDisabledRoverDialog : HomeEffect()
}