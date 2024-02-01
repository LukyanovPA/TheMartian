package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.domain.usecase.GetLatestPhotos
import com.pavellukyanov.themartian.ui.base.Reducer

class GalleryReducer(
    private val getLatestPhotos: GetLatestPhotos
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {
    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
                saveState(oldState.copy(roverName = action.roverName))
                loadLatest(roverName = action.roverName)
            }

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.GoBack)
        }
    }

    private fun loadLatest(roverName: String) = io {
        actionWithState { currentState ->
            saveState(currentState.copy(isLoading = false, photos = getLatestPhotos(roverName)))
        }
    }
}