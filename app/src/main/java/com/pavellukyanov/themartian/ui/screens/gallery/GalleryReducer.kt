package com.pavellukyanov.themartian.ui.screens.gallery

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.GetFavourites
import com.pavellukyanov.themartian.domain.usecase.GetRoversOnFavourites
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.PhotoToCache
import com.pavellukyanov.themartian.domain.usecase.UpdateCamerasCache
import com.pavellukyanov.themartian.ui.base.Reducer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest

class GalleryReducer(
    private val photoToCache: PhotoToCache,
    private val loadPhotos: LoadPhotos,
    private val getCameras: GetCameras,
    private val updateCamerasCache: UpdateCamerasCache,
    private val getFavourites: GetFavourites,
    private val getRoversOnFavourites: GetRoversOnFavourites
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {

    private var isLoadingLocked = false

    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.InitGallery -> handleInit(oldState, action.roverName, action.isLocal)
            is GalleryAction.LoadPage -> handleLoadPage(oldState, page = action.page, isLatest = oldState.isLatest)
            is GalleryAction.OnSetNewOptions -> handleNewOptions(oldState, action.newOptions)
            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> onSaveSelectedPhoto(action.photoDto)
            is GalleryAction.OnImageError -> onError(action.error)
            is GalleryAction.OnChooseRover -> execute(oldState.copy(chooseRover = action.rover))
        }
    }

    private suspend fun handleInit(oldState: GalleryState, roverName: String, isLocal: Boolean) {
        if (isLocal) {
            execute(oldState.copy(isLoading = true, isLocal = true))
            onLoadFavouritesRovers()
            onSubscribeFavourites()
            return
        }

        execute(
            oldState.copy(
                isLoading = true,
                isLocal = false,
                isLatest = true,
                options = oldState.options.copy(roverName = roverName)
            )
        )

        onLoadCameras()
        handleLoadPage(_state.value, page = 1, isLatest = true)
    }

    private suspend fun onLoadCameras() {
        execute(_state.value.copy(cameras = getCameras.invokeOnce(options = _state.value.options)))
    }

    private suspend fun handleLoadPage(oldState: GalleryState, page: Int, isLatest: Boolean) {
        if (isLoadingLocked) return
        isLoadingLocked = true
        try {
            val result = loadPhotos(options = oldState.options, page = page, isLatest = isLatest)

            if (page == 1 && result.photos.isNotEmpty()) {
                updateCamerasCache(photos = result.photos)
            }

            // The date label reflects the newest loaded page, so paginating deeper
            // must not move it back to an older sol.
            val newestPhoto = result.photos.firstOrNull().takeIf { page == 1 }

            execute(
                _state.value.copy(
                    isLoading = false,
                    canPaginate = result.canPaginate,
                    options = _state.value.options.copy(
                        date = newestPhoto?.earthDate ?: _state.value.options.date,
                        displayDate = newestPhoto?.earthFormattedDate ?: _state.value.options.displayDate
                    ),
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
        execute(
            oldState.copy(
                isLoading = true,
                isLatest = false,
                options = newOptions,
                photos = listOf(),
                page = 1
            )
        )
        handleLoadPage(_state.value, page = 1, isLatest = false)
    }

    private fun onSaveSelectedPhoto(photo: Photo) = cpu {
        photoToCache(photo)
        sendEffect(GalleryEffect.OnPhotoClick(photoId = photo.id))
    }

    /** Favourites live only in the local cache, so the list is observed instead of paginated. */
    @OptIn(ExperimentalCoroutinesApi::class)
    private fun onSubscribeFavourites() = cpu {
        _state
            .flatMapLatest { state -> getFavourites(roverName = state.chooseRover.orEmpty()) }
            .collect { photos ->
                execute(_state.value.copy(isLoading = false, photos = photos))
            }
    }

    private fun onLoadFavouritesRovers() = cpu {
        execute(_state.value.copy(rovers = getRoversOnFavourites()))
    }
}
