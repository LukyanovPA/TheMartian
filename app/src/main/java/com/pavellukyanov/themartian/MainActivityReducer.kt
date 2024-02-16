package com.pavellukyanov.themartian

import coil.annotation.ExperimentalCoilApi
import coil.imageLoader
import com.pavellukyanov.themartian.ui.base.Reducer
import com.pavellukyanov.themartian.utils.C.DB_NAME
import com.pavellukyanov.themartian.utils.ext.debug
import java.io.File

class MainActivityReducer : Reducer<MainState, MainAction, MainEffect>(MainState()) {

    init {
        onCheckCacheSize()
        cpu {
            state.collect { s ->
                debug { "CACHE img ${s.imageCacheSize} | db ${s.databaseSize}" }
            }
        }
    }

    override suspend fun reduce(oldState: MainState, action: MainAction) {
        when (action) {
            is MainAction.Error -> sendEffect(MainEffect.ShowError(errorMessage = action.error.message.orEmpty()))
            is MainAction.CloseErrorDialog -> sendEffect(MainEffect.CloseErrorDialog)
        }
    }

    @OptIn(ExperimentalCoilApi::class)
    private fun onCheckCacheSize() = io {
        //Delete Coil Cache
        //context.imageLoader.diskCache?.clear()
        //context.imageLoader.memoryCache?.clear()

        val currentImageCacheSize = ((context.imageLoader.diskCache?.size ?: 0L) / 1024) / 1024

        //Delete Room Cache
        //context.deleteDatabase("MartianLocalDatabase.db")
        val currentDbSize = getRoomDatabaseSize()

        withState { currentState ->
            saveState(currentState.copy(imageCacheSize = currentImageCacheSize, databaseSize = currentDbSize))
        }
    }

    @Throws(Exception::class)
    private fun getRoomDatabaseSize(): Long {
        val dbFolderPath = context.filesDir.absolutePath.replace("files", "databases")
        val dbFile = File("$dbFolderPath/$DB_NAME")

        if (!dbFile.exists()) throw Exception("${dbFile.absolutePath} doesn't exist")

        return (dbFile.length() / 1024) / 1024
    }
}