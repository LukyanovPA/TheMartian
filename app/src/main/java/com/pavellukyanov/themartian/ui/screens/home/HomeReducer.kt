package com.pavellukyanov.themartian.ui.screens.home

import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.IsRoverDataAvailable
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.utils.ErrorQueue
import com.pavellukyanov.themartian.utils.UiError
import kotlinx.coroutines.flow.combine

/** How many recent frames a rover card previews — see [HomeReducer.onLoadThumbnail]. */
private const val THUMBNAIL_COUNT = 4

class HomeReducer(
    private val loadRovers: LoadRovers,
    private val loadPhotos: LoadPhotos,
    private val isRoverDataAvailable: IsRoverDataAvailable,
    private val errorQueue: ErrorQueue
) : Reducer<HomeState, HomeAction, HomeEffect>(HomeState()) {
    override suspend fun reduce(oldState: HomeState, action: HomeAction) {
        when (action) {
            is HomeAction.LoadRovers -> onLoadRovers()
            is HomeAction.OnRoverClick -> {
                if (isRoverDataAvailable(rover = action.rover)) sendEffect(HomeEffect.NavigateToRoverGallery(roverName = action.rover.roverName))
                else sendEffect(HomeEffect.ShowDisabledRoverDialog)
            }
            is HomeAction.LoadThumbnail -> onLoadThumbnail(roverName = action.roverName)
        }
    }

    /**
     * Best-effort preview strip for a rover card — the most recent frames, nothing more. Runs
     * once the roster is already on screen, never gates it and never reports failure: a slow or
     * broken connection just leaves that card without a preview, exactly like today, instead of
     * adding another thing that can hold the screen hostage (see the splash-hang investigation).
     */
    private suspend fun onLoadThumbnail(roverName: String) {
        if (oldStateHasThumbnail(roverName)) return
        try {
            val result = loadPhotos(
                options = PhotosOptions(roverName = roverName, perPage = THUMBNAIL_COUNT),
                page = 1,
                isLatest = true
            )
            if (result.photos.isNotEmpty()) {
                execute(_state.value.copy(thumbnails = _state.value.thumbnails + (roverName to result.photos)))
            }
        } catch (_: Exception) {
            // Decorative only — leave the card without a preview strip.
        }
    }

    private fun oldStateHasThumbnail(roverName: String) = _state.value.thumbnails.containsKey(roverName)

    /**
     * The rovers come from the cache, so an empty list normally means "not synced yet". That
     * stops being true the moment a sync fails: there is nothing left to wait for, and the
     * screen would sit on its spinner for good. The error queue is folded in here rather than
     * watched by a second collector, so one dispatch still means one subscriber.
     *
     * [syncFailed] is latched because the queue is cleared again once the user dismisses the
     * message — without it, dismissing would put the spinner back and hide the empty screen.
     */
    private fun onLoadRovers() = cpu {
        var syncFailed = false

        combine(loadRovers(), errorQueue.onError) { rovers, error -> rovers to error }
            .collect { (rovers, error) ->
                if (error is UiError.Error) syncFailed = true
                execute(_state.value.copy(isLoading = rovers.isEmpty() && !syncFailed, rovers = rovers))
            }
    }
}