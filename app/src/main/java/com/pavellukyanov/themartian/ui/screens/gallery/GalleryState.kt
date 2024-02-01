package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING

data class GalleryState(
    override val isLoading: Boolean = true,
    val photos: List<PhotoDto> = listOf(),
    val roverName: String = EMPTY_STRING
) : State()

sealed class GalleryAction : Action() {
    data class LoadLatestPhotos(val roverName: String) : GalleryAction()
    data object OnBackClick : GalleryAction()
}

sealed class GalleryEffect : Effect() {
    data object GoBack : GalleryEffect()
}