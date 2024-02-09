package com.pavellukyanov.themartian.ui.screens.gallery

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.PhotoToCache
import com.pavellukyanov.themartian.domain.usecase.PhotosDataSource
import com.pavellukyanov.themartian.ui.base.Reducer

class GalleryReducer(
    private val photoToCache: PhotoToCache,
    private val getCameras: GetCameras
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {

    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
                val newOptions = oldState.options.copy(roverName = action.roverName)
                saveState(oldState.copy(options = newOptions, isLocal = action.isLocal))
                onLoadPhotos(options = newOptions, isLatest = true)
            }

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> obSaveSelectedPhoto(action.photoDto)
            is GalleryAction.OnSetNewDate -> {
                val newOptions = oldState.options.copy(date = action.newDate)
                saveState(oldState.copy(isLoading = true, options = newOptions))
                onLoadPhotos(options = newOptions, isLatest = false)
            }
        }
    }

    private fun onLoadPhotos(options: PhotosOptions, isLatest: Boolean) = withState { currentState ->
        saveState(
            currentState.copy(
                isLoading = false,
                photos = Pager(
                    config = PagingConfig(pageSize = 25),
                    initialKey = 1,
                    pagingSourceFactory = {
                        PhotosDataSource(
                            options = options,
                            isLatest = isLatest,
                            currentDate = ::handleCurrentDate
                        )
                    }
                ).flow.cachedIn(viewModelScope)

            )
        )
        onSubscribeCameras(options)
    }

    private fun handleCurrentDate(date: String) = withState { currentState ->
        saveState(currentState.copy(options = currentState.options.copy(date = date)))
    }

    private fun obSaveSelectedPhoto(photo: Photo) = cpu {
        photoToCache(photo)
        sendEffect(GalleryEffect.OnPhotoClick(photoId = photo.id))
    }

    private fun onSubscribeCameras(options: PhotosOptions) = withState { currentState ->
        getCameras(options = options)
            .collect { cameras ->
                saveState(currentState.copy(cameras = cameras))
            }
    }
}