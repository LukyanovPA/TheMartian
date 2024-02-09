package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.CamerasDao
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.utils.ext.onMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetCameras(
    private val camerasDao: CamerasDao
) {
    operator fun invoke(options: PhotosOptions): Flow<List<Camera>> =
        camerasDao.allStream()
            .flowOn(Dispatchers.IO)
            .onMap { list ->
                list.filter { it.roverName == options.roverName }
            }
            .onMap { list ->
                if (options.camera != null) list.filter { it.name == options.camera } else list
            }
}