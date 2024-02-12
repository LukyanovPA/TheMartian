package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.PhotoToCache
import com.pavellukyanov.themartian.ui.base.Reducer

class GalleryReducer(
    private val photoToCache: PhotoToCache,
    private val getCameras: GetCameras,
    private val loadPhotos: LoadPhotos
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {

    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
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

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> onSaveSelectedPhoto(action.photoDto)
            is GalleryAction.OnSetNewOptions -> {
                saveState(
                    oldState.copy(
                        isLoading = true,
                        options = action.newOptions,
                        isLatest = false,
                        page = 1
                    )
                )
                onLoadPhotos(options = action.newOptions, page = 1, isLatest = false)
            }

            is GalleryAction.LoadMore -> {
                onLoadPhotos(options = oldState.options, page = oldState.page, isLatest = oldState.isLatest)
            }
        }
    }

    private suspend fun onLoadPhotos(options: PhotosOptions, page: Int, isLatest: Boolean) {
        onSubscribeCameras(options = options)
        val photos = loadPhotos(options = options, page = page, isLatest = isLatest)
        val date = if (photos.isNotEmpty()) photos.firstOrNull()?.earthDate.orEmpty() else _state.value.options.date
        val displayDate = if (photos.isNotEmpty()) photos.firstOrNull()?.earthFormattedDate.orEmpty() else _state.value.options.displayDate

        val newList = _state.value.photos
        val canPaginate = photos.size == 25
        newList.apply {
            if (page == 1) clear()
            addAll(photos)
        }

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
}