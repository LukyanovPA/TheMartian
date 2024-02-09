package com.pavellukyanov.themartian.ui.screens.photo

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.usecase.ChangeFavourites
import com.pavellukyanov.themartian.domain.usecase.IsFavourites
import com.pavellukyanov.themartian.domain.utils.Storage
import com.pavellukyanov.themartian.ui.base.Reducer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class PhotoReducer(
    private val storage: Storage<Photo?>,
    private val isFavourites: IsFavourites,
    private val changeFavourites: ChangeFavourites
) : Reducer<PhotoState, PhotoAction, PhotoEffect>(PhotoState()) {

    override suspend fun reduce(oldState: PhotoState, action: PhotoAction) {
        when (action) {
            is PhotoAction.LoadPhoto -> onSubscribeStorage()
            is PhotoAction.OnBackClick -> sendEffect(PhotoEffect.OnBackClick)
            is PhotoAction.DownloadPhoto -> onDownloadPhoto(photo = action.photo)
            is PhotoAction.ChangeFavourites -> action.photo?.let { onChangeFavourites(isAdd = !oldState.isFavourites, photo = it) }
        }
    }

    private fun onSubscribeStorage() = withState { currentState ->
        storage.observe()
            .filter { it != null }
            .map { it!! }
            .flowOn(Dispatchers.Default)
            .collect { photo ->
                onSubscribeIsFavourites(id = photo.id)
                saveState(currentState.copy(photo = photo))
            }
    }

    private fun onSubscribeIsFavourites(id: Int) = withState { currentState ->
        isFavourites(id = id)
            .collect { state ->
                saveState(currentState.copy(isFavourites = state))
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