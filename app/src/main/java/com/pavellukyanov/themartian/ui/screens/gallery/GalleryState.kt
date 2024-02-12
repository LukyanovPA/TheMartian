package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

data class GalleryState(
    override val isLoading: Boolean = false,
    val options: PhotosOptions = PhotosOptions(),
    val isLocal: Boolean = false,
    val cameras: List<Camera> = listOf(),
    val canPaginate: Boolean = false,
    val photos: MutableList<Photo> = mutableListOf(),
    val isLatest: Boolean = false,
    val page: Int = 1
) : State()

sealed class GalleryAction : Action() {
    data class LoadLatestPhotos(val roverName: String, val isLocal: Boolean) : GalleryAction()
    data object OnBackClick : GalleryAction()
    data class OnPhotoClick(val photoDto: Photo) : GalleryAction()
    data class OnSetNewOptions(val newOptions: PhotosOptions) : GalleryAction()
    data object LoadMore : GalleryAction()
}

sealed class GalleryEffect : Effect() {
    data object OnBackClick : GalleryEffect()
    data class OnPhotoClick(val photoId: Int) : GalleryEffect()
}