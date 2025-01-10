package com.pavellukyanov.themartian

import androidx.work.OutOfQuotaPolicy.*
import coil.annotation.ExperimentalCoilApi
import com.pavellukyanov.themartian.domain.entity.CacheItem
import com.pavellukyanov.themartian.domain.usecase.DeleteCameraCache
import com.pavellukyanov.themartian.domain.usecase.DeleteOldCachedPhoto
import com.pavellukyanov.themartian.domain.usecase.DeleteRoverInfoCache
import com.pavellukyanov.themartian.domain.usecase.IsEmptyRoverCache
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.ui.theme.DbPink
import com.pavellukyanov.themartian.ui.theme.MediaRed
import com.pavellukyanov.themartian.utils.C.CACHE_SIZE
import com.pavellukyanov.themartian.utils.C.DB_NAME
import com.pavellukyanov.themartian.utils.C.DEFAULT_CACHE_SIZE
import com.pavellukyanov.themartian.utils.ErrorQueue
import com.pavellukyanov.themartian.utils.UiError
import com.pavellukyanov.themartian.utils.ext.log
import com.pavellukyanov.themartian.utils.helpers.DatabaseHelper
import com.pavellukyanov.themartian.utils.helpers.ImageLoaderHelper
import com.pavellukyanov.themartian.utils.helpers.SharedPreferencesHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File

class MainActivityReducer(
    private val deleteOldCachedPhoto: DeleteOldCachedPhoto,
    private val deleteRoverInfoCache: DeleteRoverInfoCache,
    private val deleteCameraCache: DeleteCameraCache,
    private val isEmptyRoverCache: IsEmptyRoverCache,
    private val sharedPreferencesHelper: SharedPreferencesHelper,
    private val databaseHelper: DatabaseHelper,
    private val imageLoaderHelper: ImageLoaderHelper,
    private val errorQueue: ErrorQueue
) : Reducer<MainState, MainAction, MainEffect>(MainState()) {
    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    override suspend fun reduce(oldState: MainState, action: MainAction) {
        when (action) {
            is MainAction.OnStart -> {
                handleError()
                onHandleCacheState()
                sendEffect(MainEffect.UpdateRoverInfoCache)
            }

            is MainAction.Error -> sendEffect(MainEffect.ShowError(errorMessage = action.error.message ?: action.error.javaClass.simpleName))
            is MainAction.CloseErrorDialog -> errorQueue.clear()
            is MainAction.OnDeleteCache -> deleteCache()
            is MainAction.OnUpdateSettings -> updateSettings()
            is MainAction.OnCacheSizeChange -> onChangeCacheSize(size = action.size)
            is MainAction.OnFavouritesClick -> sendEffect(MainEffect.OpenFavourites)
        }
    }

    private fun handleError() = cpu {
        errorQueue.onError.collect { state ->
            when (state) {
                is UiError.Error -> sendEffect(MainEffect.ShowError(errorMessage = state.error.message.orEmpty()))
                is UiError.NoError -> sendEffect(MainEffect.CloseErrorDialog)
            }
            execute(_state.value.copy(settingButtonVisibility = state is UiError.NoError))
        }
    }

    private fun onHandleCacheState() = cpu {
        isEmptyRoverCache()
            .collect(_isLoading)
    }

    private fun onChangeCacheSize(size: Float) = cpu {
        sharedPreferencesHelper.put(CACHE_SIZE, size)
        updateSettings()
    }

    @Throws(Exception::class)
    private fun getRoomDatabaseSize(): Long =
        try {
            if (databaseHelper.canRead()) {
                val dbFolderPath = databaseHelper.getFolderPath()
                val dbFile = File("$dbFolderPath/$DB_NAME")

                if (!dbFile.exists()) throw Exception("${dbFile.absolutePath} doesn't exist")

                (dbFile.length() / 1024) / 1024
            } else {
                0
            }
        } catch (e: Throwable) {
            log.e(e)
            0
        }


    @OptIn(ExperimentalCoilApi::class)
    private fun getImageCacheSize(): Long =
        ((imageLoaderHelper.getDiskCache()?.size ?: 0L) / 1024) / 1024

    private fun updateSettings() = io {
        val mediaSize = getImageCacheSize()
        val dbSize = getRoomDatabaseSize()
        val cacheSize = sharedPreferencesHelper.get(CACHE_SIZE, DEFAULT_CACHE_SIZE)

        execute(
            _state.value.copy(
                cacheItems = mutableListOf<CacheItem>().apply {
                    //Images
                    add(
                        CacheItem(
                            sizeMb = mediaSize,
                            chartValue = mediaSize.toFloat(),
                            chartColor = MediaRed,
                            title = R.string.cache_images_title
                        )
                    )

                    //Database
                    add(
                        CacheItem(
                            sizeMb = dbSize,
                            chartValue = dbSize.toFloat(),
                            chartColor = DbPink,
                            title = R.string.cache_db_title
                        )
                    )
                },
                currentCacheSize = cacheSize
            )
        )
    }

    private fun deleteCache() = cpu {
        onDeleteCache()
        updateSettings()
        sendEffect(MainEffect.UpdateRoverInfoCache)
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun onDeleteCache() = cpu {
        io {
            imageLoaderHelper.getDiskCache()?.clear()
            imageLoaderHelper.getMemoryCache()?.clear()
        }
        deleteOldCachedPhoto()
        deleteRoverInfoCache()
        deleteCameraCache()
    }
}