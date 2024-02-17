package com.pavellukyanov.themartian

import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.pavellukyanov.themartian.domain.entity.CacheItem
import com.pavellukyanov.themartian.domain.usecase.DeleteCameraCache
import com.pavellukyanov.themartian.domain.usecase.DeleteOldCachedPhoto
import com.pavellukyanov.themartian.domain.usecase.DeleteRoverInfoCache
import com.pavellukyanov.themartian.domain.usecase.UpdateRoverInfoCache
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.ui.theme.DbPink
import com.pavellukyanov.themartian.ui.theme.MediaRed
import com.pavellukyanov.themartian.utils.C.CACHE_SIZE
import com.pavellukyanov.themartian.utils.C.DB_NAME
import com.pavellukyanov.themartian.utils.C.DEFAULT_CACHE_SIZE
import java.io.File

class MainActivityReducer(
    private val deleteOldCachedPhoto: DeleteOldCachedPhoto,
    private val deleteRoverInfoCache: DeleteRoverInfoCache,
    private val deleteCameraCache: DeleteCameraCache,
    private val updateRoverInfoCache: UpdateRoverInfoCache
) : Reducer<MainState, MainAction, MainEffect>(MainState()) {
    init {
        updateSettings()
    }

    override suspend fun reduce(oldState: MainState, action: MainAction) {
        when (action) {
            is MainAction.Error -> sendEffect(MainEffect.ShowError(errorMessage = action.error.message ?: action.error.javaClass.simpleName))
            is MainAction.CloseErrorDialog -> sendEffect(MainEffect.CloseErrorDialog)
            is MainAction.OnDeleteCache -> deleteCache()
            is MainAction.OnUpdateSettings -> updateSettings()
            is MainAction.OnCacheSizeChange -> onChangeCacheSize(size = action.size)
            is MainAction.CheckCacheOverSize -> onCheckCacheIverSize()
        }
    }

    private fun onCheckCacheIverSize() = io {
        if (getImageCacheSize() > prefs.getFloat(CACHE_SIZE, DEFAULT_CACHE_SIZE).toLong()) onDeleteCache()
    }

    private fun onChangeCacheSize(size: Float) = io {
        prefs.edit().putFloat(CACHE_SIZE, size).apply()
        updateSettings()
    }

    @Throws(Exception::class)
    private fun getRoomDatabaseSize(): Long {
        val dbFolderPath = context.filesDir.absolutePath.replace("files", "databases")
        val dbFile = File("$dbFolderPath/$DB_NAME")

        if (!dbFile.exists()) throw Exception("${dbFile.absolutePath} doesn't exist")

        return (dbFile.length() / 1024) / 1024
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun getImageCacheSize(): Long =
        ((context.imageLoader.diskCache?.size ?: 0L) / 1024) / 1024

    private fun updateSettings() = io {
        val mediaSize = getImageCacheSize()
        val dbSize = getRoomDatabaseSize()
        val cacheSize = prefs.getFloat(CACHE_SIZE, DEFAULT_CACHE_SIZE)

        withState { currentState ->
            saveState(
                currentState.copy(
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
    }

    private fun deleteCache() = cpu {
        onDeleteCache()
        updateSettings()
        updateRoverInfoCache()
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun onDeleteCache() = io {
        context.imageLoader.diskCache?.clear()
        context.imageLoader.memoryCache?.clear()
        deleteOldCachedPhoto()
        deleteRoverInfoCache()
        deleteCameraCache()
    }
}