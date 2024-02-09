package com.pavellukyanov.themartian.ui.screens.gallery

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.PhotosDataSource
import com.pavellukyanov.themartian.domain.utils.Storage
import com.pavellukyanov.themartian.ui.base.Reducer

class GalleryReducer(
    private val storage: Storage<Photo?>
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {
    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
                val newOptions = oldState.options.copy(roverName = action.roverName)
                saveState(oldState.copy(options = newOptions, isLocal = action.isLocal))
                onLoadPhotos(options = newOptions, isLatest = true)
            }

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> setPhotoToStorage(action.photoDto)
            is GalleryAction.OnSetNewDate -> {
                val newOptions = oldState.options.copy(date = action.newDate)
                saveState(oldState.copy(isLoading = true, options = newOptions))
                onLoadPhotos(options = newOptions, isLatest = false)
            }
        }
    }

    private suspend fun onLoadPhotos(options: PhotosOptions, isLatest: Boolean) = cpu {
        val pagedData = Pager(
            config = PagingConfig(pageSize = 25),
            initialKey = 1,
            pagingSourceFactory = { PhotosDataSource(options = options, isLatest = isLatest, currentDate = ::handleCurrentDate) }
        ).flow.cachedIn(viewModelScope)

        withState { currentState ->
            saveState(currentState.copy(isLoading = false, photos = pagedData))
        }
    }

    private fun handleCurrentDate(date: String) = withState { currentState ->
        saveState(currentState.copy(options = currentState.options.copy(date = date)))
    }

    private fun setPhotoToStorage(photo: Photo) = cpu {
        storage.set(photo)
        sendEffect(GalleryEffect.OnPhotoClick)
    }
}