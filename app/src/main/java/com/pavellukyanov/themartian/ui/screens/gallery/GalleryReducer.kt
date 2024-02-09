package com.pavellukyanov.themartian.ui.screens.gallery

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.GetCameras
import com.pavellukyanov.themartian.domain.usecase.PhotoToCache
import com.pavellukyanov.themartian.domain.usecase.PhotosDataSource
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.utils.C.EMPTY_STRING
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class GalleryReducer(
    private val photoToCache: PhotoToCache,
    private val getCameras: GetCameras
) : Reducer<GalleryState, GalleryAction, GalleryEffect>(GalleryState()) {
    private val currentDate = MutableStateFlow(EMPTY_STRING)

    init {
        cpu {
            currentDate.collect { date ->
                saveState(state.value.copy(isLoading = false, options = state.value.options.copy(date = date)))
            }
        }
    }

    override suspend fun reduce(oldState: GalleryState, action: GalleryAction) {
        when (action) {
            is GalleryAction.LoadLatestPhotos -> {
                val newOptions = oldState.options.copy(roverName = action.roverName)
                saveState(
                    oldState.copy(
                        isLoading = true,
                        options = newOptions,
                        isLocal = action.isLocal,
                        photos = onLoadPhotos(options = newOptions, isLatest = true)
                    )
                )
//                onLoadPhotos(options = newOptions, isLatest = true)
            }

            is GalleryAction.OnBackClick -> sendEffect(GalleryEffect.OnBackClick)
            is GalleryAction.OnPhotoClick -> onSaveSelectedPhoto(action.photoDto)
            is GalleryAction.OnSetNewOptions -> {
                saveState(
                    oldState.copy(
                        isLoading = true,
                        options = action.newOptions,
                        photos = onLoadPhotos(options = action.newOptions, isLatest = false)
                    )
                )
//                onLoadPhotos(options = action.newOptions, isLatest = false)
            }
        }
    }

    private fun onLoadPhotos(options: PhotosOptions, isLatest: Boolean): Flow<PagingData<Photo>> {
//        saveState(
//            state.value.copy(
////                isLoading = false,
//                photos = Pager(
//                    config = PagingConfig(pageSize = 25),
//                    initialKey = 1,
//                    pagingSourceFactory = {
//                        PhotosDataSource(
//                            options = options,
//                            isLatest = isLatest,
//                            currentDate = ::handleCurrentDate
//                        )
//                    }
//                ).flow,
//                options = options
//            )
//        )
        onSubscribeCameras(options)
        return Pager(
            config = PagingConfig(pageSize = 25),
            initialKey = 1,
            pagingSourceFactory = {
                PhotosDataSource(
                    options = options,
                    isLatest = isLatest,
                    currentDate = ::handleCurrentDate
                )
            }
        ).flow
    }

    private fun handleCurrentDate(date: String) {
        currentDate.value = date
    }

    private fun onSaveSelectedPhoto(photo: Photo) = cpu {
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