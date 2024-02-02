package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.domain.usecase.GetLatestPhotos
import com.pavellukyanov.themartian.domain.utils.Storage
import com.pavellukyanov.themartian.ui.base.Reducer

class GalleryReducer(
    private val getLatestPhotos: GetLatestPhotos,
    private val storage: Storage<PhotoDto?>
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {
    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
                saveState(oldState.copy(roverName = action.roverName))
                onLoadLatest(roverName = action.roverName)
            }

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> setPhotoToStorage(action.photoDto)
        }
    }

    private fun onLoadLatest(roverName: String) = io {
        withState { currentState ->
            saveState(currentState.copy(isLoading = false, photos = getLatestPhotos(roverName)))
        }
    }

    private fun setPhotoToStorage(photoDto: PhotoDto) = cpu {
        storage.set(photoDto)
        sendEffect(GalleryEffect.OnPhotoClick)
    }
}