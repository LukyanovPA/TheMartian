package com.pavellukyanov.themartian.ui.screens.gallery

import androidx.paging.PagingData
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class GalleryState(
    override val isLoading: Boolean = true,
    val photos: Flow<PagingData<Photo>> = flowOf(),
    val options: PhotosOptions = PhotosOptions(),
    val isLocal: Boolean = false
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