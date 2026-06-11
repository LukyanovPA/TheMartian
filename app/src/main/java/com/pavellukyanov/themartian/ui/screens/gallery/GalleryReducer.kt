package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.PhotoToCache
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.UpdateCamerasCache
import com.pavellukyanov.themartian.ui.base.Reducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.isActive

class GalleryReducer(
    private val photoToCache: PhotoToCache,
    private val loadPhotos: LoadPhotos,
    private val getCameras: GetCameras,
    private val updateCamerasCache: UpdateCamerasCache
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {

    private var isLoadingLocked = false

    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.InitGallery -> handleInit(oldState, action.roverName, action.isLocal)
            is GalleryAction.LoadCameras -> handleLoadCameras(oldState)
            is GalleryAction.LoadPage -> handleLoadPage(oldState, action.page)
            is GalleryAction.OnSetNewOptions -> handleNewOptions(oldState, action.newOptions)
            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> onSaveSelectedPhoto(action.photoDto)
            is GalleryAction.OnImageError -> onError(action.error)
            is GalleryAction.OnChooseRover -> execute(oldState.copy(chooseRover = action.rover))
        }
    }

    private suspend fun handleInit(oldState: GalleryState, roverName: String, isLocal: Boolean) {
        if (isLocal) return
        execute(oldState.copy(isLoading = true, options = oldState.options.copy(roverName = roverName)))
        handleLoadPage(_state.value, 1)
    }

    private suspend fun handleLoadCameras(oldState: GalleryState) {
        val cameras = getCameras.invokeOnce(options = oldState.options)
        execute(oldState.copy(cameras = cameras))
    }

    private suspend fun handleLoadPage(oldState: GalleryState, page: Int) {
        if (isLoadingLocked) return
        isLoadingLocked = true
        try {
            val result = loadPhotos(options = oldState.options, page = page, isLatest = true)

            if (page == 1 && result.photos.isNotEmpty()) {
                updateCamerasCache(photos = result.photos)
            }

            val date = result.photos.firstOrNull()?.earthDate.orEmpty()
            val displayDate = result.photos.firstOrNull()?.earthFormattedDate.orEmpty()

            execute(
                _state.value.copy(
                    isLoading = false,
                    canPaginate = result.canPaginate,
                    options = _state.value.options.copy(date = date, displayDate = displayDate),
                    page = if (result.canPaginate) _state.value.page + 1 else _state.value.page,
                    photos = if (page == 1) result.photos else _state.value.photos + result.photos
                )
            )
        } catch (_: Exception) {
            execute(_state.value.copy(isLoading = false))
        } finally {
            isLoadingLocked = false
        }
    }

    private suspend fun handleNewOptions(oldState: GalleryState, newOptions: PhotosOptions) {
        isLoadingLocked = false
        execute(oldState.copy(isLoading = true, options = newOptions, photos = listOf(), page = 1))
        handleLoadPage(_state.value, 1)
    }

    private fun onSaveSelectedPhoto(photo: Photo) = cpu {
        photoToCache(photo)
        sendEffect(GalleryEffect.OnPhotoClick(photoId = photo.id))
    }
}
