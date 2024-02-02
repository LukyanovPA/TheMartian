package com.pavellukyanov.themartian.ui.screens.photo

import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

data class PhotoState(
    override val isLoading: Boolean = false,
    val photoDto: PhotoDto? = null,
    val isFavourites: Boolean = false
) : State()

sealed class PhotoAction : Action() {
    data object LoadPhoto : PhotoAction()
    data object OnBackClick : PhotoAction()
    data class DownloadPhoto(val photo: PhotoDto) : PhotoAction()
    data class ChangeFavourites(val photo: PhotoDto) : PhotoAction()
}

sealed class PhotoEffect : Effect() {
    data object OnBackClick : PhotoEffect()
}