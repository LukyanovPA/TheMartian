package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING

data class GalleryState(
    override val isLoading: Boolean = true,
    val photos: List<Photo> = listOf(),
    val roverName: String = EMPTY_STRING,
    val currentDate: String = EMPTY_STRING
) : State()

sealed class GalleryAction : Action() {
    data class LoadLatestPhotos(val roverName: String, val isLocal: Boolean) : GalleryAction()
    data object OnBackClick : GalleryAction()
    data class OnPhotoClick(val photoDto: Photo) : GalleryAction()
    data class OnSetNewDate(val newDate: String) : GalleryAction()
}

sealed class GalleryEffect : Effect() {
    data object OnBackClick : GalleryEffect()
    data object OnPhotoClick : GalleryEffect()
}

data class PhotosFilter(
    val roverName: String,
    val date: String,
    val cameras: List<String>,

)