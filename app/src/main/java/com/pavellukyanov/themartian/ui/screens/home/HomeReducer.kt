package com.pavellukyanov.themartian.ui.screens.home

import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.utils.ErrorQueue
import com.pavellukyanov.themartian.utils.UiError
import kotlinx.coroutines.flow.combine

private const val THUMBNAIL_COUNT = 4

class HomeReducer(
    private val loadRovers: LoadRovers,
    private val loadPhotos: LoadPhotos,
    private val errorQueue: ErrorQueue
) : Reducer<HomeState, HomeAction, HomeEffect>(HomeState()) {
    override suspend fun reduce(oldState: HomeState, action: HomeAction) {
        when (action) {
            is HomeAction.LoadRovers -> onLoadRovers()
            is HomeAction.OnRoverClick -> sendEffect(HomeEffect.NavigateToRoverGallery(roverName = action.rover.roverName))
            is HomeAction.LoadThumbnail -> onLoadThumbnail(roverName = action.roverName)
        }
    }

    private suspend fun onLoadThumbnail(roverName: String) {
        if (oldStateHasThumbnail(roverName)) return
        try {
            val result = loadPhotos(
                options = PhotosOptions(roverName = roverName, perPage = THUMBNAIL_COUNT),
                page = 1,
                isLatest = true
            )
            if (result.photos.isNotEmpty()) {
                execute(_state.value.copy(thumbnails = _state.value.thumbnails + (roverName to result.photos)))
            }
        } catch (_: Exception) {
        }
    }

    private fun oldStateHasThumbnail(roverName: String) = _state.value.thumbnails.containsKey(roverName)

    private fun onLoadRovers() = cpu {
        var syncFailed = false

        combine(loadRovers(), errorQueue.onError) { rovers, error -> rovers to error }
            .collect { (rovers, error) ->
                if (error is UiError.Error) syncFailed = true
                execute(_state.value.copy(isLoading = rovers.isEmpty() && !syncFailed, rovers = rovers))
            }
    }
}
