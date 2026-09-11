package com.pavellukyanov.themartian.ui.screens.photo

import android.app.DownloadManager
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.ui.base.Action
import com.pavellukyanov.themartian.ui.base.Effect
import com.pavellukyanov.themartian.ui.base.State
import com.pavellukyanov.themartian.utils.BrowseContext

data class PhotoState(
    override val isLoading: Boolean = false,
    val photo: Photo? = null,
    val isFavourites: Boolean = false,
    // Position within the gallery this photo was opened from — null when there is none to know
    // (a deep link, or the browse session was never populated). See GalleryBrowseSession.
    val browseContext: BrowseContext? = null
) : State()

sealed class PhotoAction : Action() {
    data class LoadPhoto(val photoId: Int) : PhotoAction()
    data object OnBackClick : PhotoAction()
    data class DownloadPhoto(val photo: Photo?) : PhotoAction()
    data class ChangeFavourites(val photo: Photo?) : PhotoAction()
    data class OnImageError(val error: Throwable) : PhotoAction()
    data object OnPreviousClick : PhotoAction()
    data object OnNextClick : PhotoAction()
}

sealed class PhotoEffect : Effect() {
    data object OnBackClick : PhotoEffect()
    data class OnDownload(val request: DownloadManager.Request) : PhotoEffect()
    data class NavigateToPhoto(val photoId: Int) : PhotoEffect()
}