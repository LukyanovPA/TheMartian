package com.pavellukyanov.themartian.ui.screens.home

import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.domain.usecase.LoadPhotos
import com.pavellukyanov.themartian.domain.usecase.LoadRandomPhoto
import com.pavellukyanov.themartian.domain.usecase.LoadRovers
import com.pavellukyanov.themartian.domain.usecase.UpdateRoverInfoCache
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.utils.ErrorQueue
import com.pavellukyanov.themartian.utils.UiError
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import kotlin.coroutines.cancellation.CancellationException

private const val THUMBNAIL_COUNT = 4

class HomeReducer(
    private val loadRovers: LoadRovers,
    private val loadPhotos: LoadPhotos,
    private val loadRandomPhoto: LoadRandomPhoto,
    private val updateRoverInfoCache: UpdateRoverInfoCache,
    private val errorQueue: ErrorQueue
) : Reducer<HomeState, HomeAction, HomeEffect>(HomeState()) {
    override suspend fun reduce(oldState: HomeState, action: HomeAction) {
        when (action) {
            is HomeAction.LoadRovers -> onLoadRovers()
            is HomeAction.Refresh -> onRefresh()
            is HomeAction.OnRoverClick -> sendEffect(HomeEffect.NavigateToRoverGallery(roverName = action.rover.roverName))
            is HomeAction.LoadThumbnail -> onLoadThumbnail(roverName = action.roverName)
        }
    }

    private suspend fun onLoadThumbnail(roverName: String) {
        if (oldStateHasThumbnail(roverName)) return
        fetchThumbnails(roverName)?.let { photos ->
            execute(_state.value.copy(thumbnails = _state.value.thumbnails + (roverName to photos)))
        }
    }

    private fun oldStateHasThumbnail(roverName: String) = _state.value.thumbnails.containsKey(roverName)

    private suspend fun fetchThumbnails(roverName: String): List<Photo>? =
        try {
            loadPhotos(
                options = PhotosOptions(roverName = roverName, perPage = THUMBNAIL_COUNT),
                page = 1,
                isLatest = true
            ).photos.takeIf { it.isNotEmpty() }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }

    private fun onLoadRovers() = cpu {
        var syncFailed = false
        var isHeroRequested = false

        combine(loadRovers(), errorQueue.onError) { rovers, error -> rovers to error }
            .collect { (rovers, error) ->
                if (error is UiError.Error) syncFailed = true
                execute(_state.value.copy(isLoading = rovers.isEmpty() && !syncFailed, rovers = rovers))

                if (!isHeroRequested && rovers.isNotEmpty()) {
                    isHeroRequested = true
                    cpu { loadHeroPhoto() }
                }
            }
    }

    private suspend fun onRefresh() {
        if (_state.value.isRefreshing) return
        execute(_state.value.copy(isRefreshing = true))

        try {
            try {
                updateRoverInfoCache()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
            }

            coroutineScope {
                launch { loadHeroPhoto() }

                val freshThumbnails = _state.value.rovers
                    .map { rover -> async { fetchThumbnails(rover.roverName)?.let { rover.roverName to it } } }
                    .awaitAll()
                    .filterNotNull()
                execute(_state.value.copy(thumbnails = _state.value.thumbnails + freshThumbnails))
            }
        } finally {
            execute(_state.value.copy(isRefreshing = false))
        }
    }

    private suspend fun loadHeroPhoto() {
        val photo = loadRandomPhoto(_state.value.rovers) ?: return
        execute(_state.value.copy(heroPhoto = photo))
    }
}
