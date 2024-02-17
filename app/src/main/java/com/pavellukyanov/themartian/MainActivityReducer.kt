package com.pavellukyanov.themartian

import android.content.Context.MODE_PRIVATE
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
import com.pavellukyanov.themartian.utils.C.COMMON
import com.pavellukyanov.themartian.utils.C.DB_NAME
import java.io.File

@OptIn(ExperimentalCoilApi::class)
class MainActivityReducer(
    private val deleteOldCachedPhoto: DeleteOldCachedPhoto,
    private val deleteRoverInfoCache: DeleteRoverInfoCache,
    private val deleteCameraCache: DeleteCameraCache,
    private val updateRoverInfoCache: UpdateRoverInfoCache
) : Reducer<MainState, MainAction, MainEffect>(MainState()) {
    private val prefs = context.getSharedPreferences(COMMON, MODE_PRIVATE)

    init {
        updateSettings()
    }

    override suspend fun reduce(oldState: MainState, action: MainAction) {
        when (action) {
            is MainAction.Error -> sendEffect(MainEffect.ShowError(errorMessage = action.error.message.orEmpty()))
            is MainAction.CloseErrorDialog -> sendEffect(MainEffect.CloseErrorDialog)
            is MainAction.OnDeleteCache -> deleteCache()
            is MainAction.OnUpdateSettings -> updateSettings()
            is MainAction.OnCacheSizeChange -> onChangeCacheSize(size = action.size)
        }
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
    private fun getImageCache(): Long =
        ((context.imageLoader.diskCache?.size ?: 0L) / 1024) / 1024

    private fun updateSettings() = io {
        val mediaSize = getImageCache()
        val dbSize = getRoomDatabaseSize()

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
                    currentCacheSize = prefs.getFloat(CACHE_SIZE, 100F)
                )
            )
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun deleteCache() = cpu {
        context.imageLoader.diskCache?.clear()
        context.imageLoader.memoryCache?.clear()
        updateSettings()
        deleteOldCachedPhoto()
        deleteRoverInfoCache()
        deleteCameraCache()
        updateRoverInfoCache()
    }
}