package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.CamerasDao
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.utils.ext.onCpu
import com.pavellukyanov.themartian.utils.ext.onIo
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class UpdateCamerasCache(
    private val camerasDao: CamerasDao
) {
    private val mutex = Mutex()
    private var lastList: List<Photo> = listOf()

    suspend operator fun invoke(photos: List<Photo>) = onCpu {
        if (isDifference(photos)) {
            photos.forEach { photo ->
                camerasDao.getByName(name = photo.cameraName)
                    .forEach { camera ->
                        onIo { camerasDao.update(camera.copy(cameraFullName = photo.cameraFullName)) }
                    }
            }
            mutex.withLock { lastList = photos }
        }
    }

    private suspend fun isDifference(photos: List<Photo>): Boolean = mutex.withLock {
        lastList != photos
    }
}