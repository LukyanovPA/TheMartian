package com.pavellukyanov.themartian.ui.screens.photo

import android.app.DownloadManager
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State

data class PhotoState(
    override val isLoading: Boolean = false,
    val photo: Photo? = null,
    val isFavourites: Boolean = false
) : State()

sealed class PhotoAction : Action() {
    data class LoadPhoto(val photoId: Int) : PhotoAction()
    data object OnBackClick : PhotoAction()
    data class DownloadPhoto(val photo: Photo?) : PhotoAction()
    data class ChangeFavourites(val photo: Photo?) : PhotoAction()
    data class OnImageError(val error: Throwable) : PhotoAction()
}

sealed class PhotoEffect : Effect() {
    data object OnBackClick : PhotoEffect()
    data class OnDownload(val request: DownloadManager.Request) : PhotoEffect()
}