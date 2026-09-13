package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.CamerasDao
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.domain.entity.PhotosOptions

class GetCameras(
    private val camerasDao: CamerasDao
) {
    suspend fun invokeOnce(options: PhotosOptions): List<Camera> {
        val all = camerasDao.all()
        return all
            .filter { it.roverName == options.roverName }
            .let { list ->
                if (options.camera != null) list.filter { it.name == options.camera }
                else list
            }
    }
}
