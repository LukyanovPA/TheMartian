package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.GetFavourites
import com.pavellukyanov.themartian.domain.usecase.GetRoversOnFavourites
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.PhotoToCache
import com.pavellukyanov.themartian.ui.base.Reducer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapMerge

class GalleryReducer(
    private val photoToCache: PhotoToCache,
    private val getCameras: GetCameras,
    private val loadPhotos: LoadPhotos,
    private val getFavourites: GetFavourites,
    private val getRoversOnFavourites: GetRoversOnFavourites
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {

    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
                if (action.isLocal) {
                    onLoadFavouritesRovers()
                    onSubscribeFavourites()
                    saveState(
                        oldState.copy(
                            isLoading = true,
                            isLocal = action.isLocal
                        )
                    )
                } else {
                    saveState(
                        oldState.copy(
                            isLoading = true,
                            options = oldState.options.copy(roverName = action.roverName),
                            isLocal = action.isLocal,
                            isLatest = true
                        )
                    )
                    onLoadPhotos(options = oldState.options.copy(roverName = action.roverName), page = oldState.page, isLatest = true)
                }
            }

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> onSaveSelectedPhoto(action.photoDto)
            is GalleryAction.OnSetNewOptions -> {
                saveState(
                    oldState.copy(
                        isLoading = true,
                        options = action.newOptions,
                        isLatest = false,
                        photos = mutableListOf(),
                        page = 1
                    )
                )
                onLoadPhotos(options = action.newOptions, page = 1, isLatest = false)
            }

            is GalleryAction.LoadMore -> onLoadPhotos(options = oldState.options, page = oldState.page, isLatest = oldState.isLatest)
            is GalleryAction.OnImageError -> handledError(action.error)
            is GalleryAction.OnChooseRover -> saveState(oldState.copy(chooseRover = action.rover))
        }
    }

    private suspend fun onLoadPhotos(options: PhotosOptions, page: Int, isLatest: Boolean) {
        onSubscribeCameras(options = options)
        val photos = loadPhotos(options = options, page = page, isLatest = isLatest)
        val date = if (photos.isNotEmpty()) photos.firstOrNull()?.earthDate.orEmpty() else _state.value.options.date
        val displayDate = if (photos.isNotEmpty()) photos.firstOrNull()?.earthFormattedDate.orEmpty() else _state.value.options.displayDate

        val newList = _state.value.photos
        val canPaginate = photos.size == 25
        newList.addAll(photos)

        saveState(
            _state.value.copy(
                isLoading = false,
                canPaginate = canPaginate,
                options = _state.value.options.copy(date = date, displayDate = displayDate),
                page = if (canPaginate) _state.value.page + 1 else _state.value.page,
                photos = newList
            )
        )
    }

    private fun onSaveSelectedPhoto(photo: Photo) = cpu {
        photoToCache(photo)
        sendEffect(GalleryEffect.OnPhotoClick(photoId = photo.id))
    }

    private fun onSubscribeCameras(options: PhotosOptions) = cpu {
        getCameras(options = options)
            .collect { cameras ->
                saveState(_state.value.copy(cameras = cameras))
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun onSubscribeFavourites() = io {
        _state
            .flatMapMerge { state ->
                getFavourites(roverName = state.chooseRover.orEmpty())
            }
            .collect { photos ->
                withState { currentState ->
                    saveState(currentState.copy(isLoading = false, photos = photos.toMutableList()))
                }
            }
    }

    private fun onLoadFavouritesRovers() = cpu {
        withState { currentState ->
            saveState(currentState.copy(rovers = getRoversOnFavourites()))
        }
    }
}