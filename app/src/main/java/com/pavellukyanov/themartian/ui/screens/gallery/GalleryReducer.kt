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
            is GalleryAction.LoadLatestPhotos -> handleLoadLatestPhotosAction(oldState = oldState, roverName = action.roverName, isLocal = action.isLocal)
            is GalleryAction.OnBackClick -> sendEffect(newEffect = GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> onSaveSelectedPhoto(photo = action.photoDto)
            is GalleryAction.OnSetNewOptions -> handleOnSetNewOptionsAction(oldState = oldState, action.newOptions)
            is GalleryAction.LoadMore -> onLoadPhotos(options = oldState.options, page = oldState.page, isLatest = oldState.isLatest)
            is GalleryAction.OnImageError -> onError(error = action.error)
            is GalleryAction.OnChooseRover -> execute(oldState.copy(chooseRover = action.rover))
        }
    }

    private suspend fun handleOnSetNewOptionsAction(oldState: GalleryState, newOptions: PhotosOptions) {
        execute(
            oldState.copy(
                isLoading = true,
                options = newOptions,
                isLatest = false,
                photos = mutableListOf(),
                page = 1
            )
        )
        onLoadPhotos(options = newOptions, page = 1, isLatest = false)
    }

    private suspend fun handleLoadLatestPhotosAction(oldState: GalleryState, roverName: String, isLocal: Boolean) {
        if (isLocal) {
            onLoadFavouritesRovers()
            onSubscribeFavourites()
            execute(
                oldState.copy(
                    isLoading = true,
                    isLocal = true
                )
            )
        } else {
            execute(
                oldState.copy(
                    isLoading = true,
                    options = oldState.options.copy(roverName = roverName),
                    isLocal = false,
                    isLatest = true
                )
            )
            onLoadPhotos(options = oldState.options.copy(roverName = roverName), page = oldState.page, isLatest = true)
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

        execute(
            _state.value.copy(
                isLoading = false,
                canPaginate = canPaginate,
                options = _state.value.options.copy(date = date, displayDate = displayDate),
                page = if (canPaginate) _state.value.page + 1 else _state.value.page,
                photos = newList
            )
        )
    }

    private fun onSaveSelectedPhoto(photo: Photo) = ui {
        photoToCache(photo)
        sendEffect(GalleryEffect.OnPhotoClick(photoId = photo.id))
    }

    private fun onSubscribeCameras(options: PhotosOptions) = ui {
        getCameras(options = options)
            .collect { cameras ->
                execute(_state.value.copy(cameras = cameras))
            }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun onSubscribeFavourites() = ui {
        _state
            .flatMapMerge { state ->
                getFavourites(roverName = state.chooseRover.orEmpty())
            }
            .collect { photos ->
                execute(_state.value.copy(isLoading = false, photos = photos.toMutableList()))
            }
    }

    private fun onLoadFavouritesRovers() = ui {
        execute(_state.value.copy(rovers = getRoversOnFavourites()))
    }
}