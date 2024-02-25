package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.CamerasDao
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetCameras(
    private val camerasDao: CamerasDao
) {
    operator fun invoke(options: PhotosOptions): Flow<List<Camera>> =
        camerasDao.allStream()
            .map { list ->
                list.filter { it.roverName == options.roverName }
            }
            .map { list ->
                if (options.camera != null) list.filter { it.name == options.camera } else list
            }
            .flowOn(Dispatchers.IO)
}