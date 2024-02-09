package com.pavellukyanov.themartian.ui.screens.photo

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.usecase.ChangeFavourites
import com.pavellukyanov.themartian.domain.usecase.GetPhotoById
import com.pavellukyanov.themartian.ui.base.Reducer
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map

class PhotoReducer(
    private val getPhotoById: GetPhotoById,
    private val changeFavourites: ChangeFavourites
) : Reducer<PhotoState, PhotoAction, PhotoEffect>(PhotoState()) {

    override suspend fun reduce(oldState: PhotoState, action: PhotoAction) {
        when (action) {
            is PhotoAction.LoadPhoto -> onSubscribeStorage(photoId = action.photoId)
            is PhotoAction.OnBackClick -> sendEffect(PhotoEffect.OnBackClick)
            is PhotoAction.DownloadPhoto -> onDownloadPhoto(photo = action.photo)
            is PhotoAction.ChangeFavourites -> action.photo?.let { onChangeFavourites(isAdd = !oldState.isFavourites, photo = it) }
        }
    }

    private fun onSubscribeStorage(photoId: Int) = withState { currentState ->
        getPhotoById(id = photoId)
            .filter { it != null }
            .map { it!! }
            .collect { photo ->
                saveState(currentState.copy(photo = photo, isFavourites = photo.isFavourites))
            }
    }

    private fun onDownloadPhoto(photo: Photo?) = io {
        photo?.let {
            val list = photo.src.split('/')
            val request = DownloadManager.Request(Uri.parse(photo.src))
                .setTitle(list[list.lastIndex])
                .setDescription(photo.cameraFullName)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setAllowedOverMetered(true)

            (context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager).enqueue(request)
        }
    }

    private suspend fun onChangeFavourites(isAdd: Boolean, photo: Photo) {
        if (isAdd) changeFavourites.add(photo)
        else changeFavourites.delete(photo)
    }
}