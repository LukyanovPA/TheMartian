package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.usecase.GetLatestPhotos
import com.pavellukyanov.themartian.domain.usecase.GetPhotosByEarthDateRemote
import com.pavellukyanov.themartian.domain.utils.Storage
import com.pavellukyanov.themartian.ui.base.Reducer

class GalleryReducer(
    private val getLatestPhotos: GetLatestPhotos,
    private val storage: Storage<Photo?>,
    private val getPhotosByEarthDateRemote: GetPhotosByEarthDateRemote
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {
    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
                saveState(oldState.copy(roverName = action.roverName))
                onLoadLatest(roverName = action.roverName, isLocal = action.isLocal)
            }

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> setPhotoToStorage(action.photoDto)
            is GalleryAction.OnSetNewDate -> {
                saveState(oldState.copy(isLoading = true))
                loadPhotosByNewDate(roverName = oldState.photos.first().roverName, action.newDate)
            }
        }
    }

    private fun onLoadLatest(roverName: String, isLocal: Boolean) = io {
        val latest = getLatestPhotos(roverName, isLocal)
        withState { currentState ->
            saveState(currentState.copy(isLoading = false, photos = latest, currentDate = latest.first().earthDate))
        }
    }

    private fun setPhotoToStorage(photo: Photo) = cpu {
        storage.set(photo)
        sendEffect(GalleryEffect.OnPhotoClick)
    }

    private fun loadPhotosByNewDate(roverName: String, newDate: String) = io {
        withState { currentState ->
            saveState(currentState.copy(isLoading = false, currentDate = newDate, photos = getPhotosByEarthDateRemote(roverName, newDate)))
        }
    }
}