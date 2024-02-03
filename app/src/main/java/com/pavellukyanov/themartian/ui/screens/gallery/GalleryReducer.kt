package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.usecase.GetLatestPhotos
import com.pavellukyanov.themartian.domain.utils.Storage
import com.pavellukyanov.themartian.ui.base.Reducer

class GalleryReducer(
    private val getLatestPhotos: GetLatestPhotos,
    private val storage: Storage<Photo?>
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {
    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
                saveState(oldState.copy(roverName = action.roverName))
                onLoadLatest(roverName = action.roverName, isLocal = action.isLocal)
            }

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> setPhotoToStorage(action.photoDto)
        }
    }

    private fun onLoadLatest(roverName: String, isLocal: Boolean) = io {
        withState { currentState ->
            saveState(currentState.copy(isLoading = false, photos = getLatestPhotos(roverName, isLocal)))
        }
    }

    private fun setPhotoToStorage(photo: Photo) = cpu {
        storage.set(photo)
        sendEffect(GalleryEffect.OnPhotoClick)
    }
}