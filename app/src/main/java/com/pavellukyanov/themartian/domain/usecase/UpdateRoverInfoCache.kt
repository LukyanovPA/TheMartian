package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.cache.dao.CamerasDao
import com.pavellukyanov.themartian.data.cache.dao.RoverInfoDao
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.domain.entity.Camera
import com.pavellukyanov.themartian.domain.entity.toRover
import com.pavellukyanov.themartian.utils.ext.onIo

class UpdateRoverInfoCache(
    private val roverInfoDao: RoverInfoDao,
    private val apiDataSource: ApiDataSource,
    private val camerasDao: CamerasDao
) {
    suspend operator fun invoke() {
        roverInfoDao.insert(
            apiDataSource.getRoversInfo()
                .map { updateCameras(it) }
                .map(RoverItemDto::toRover)
        )
    }

    private suspend fun updateCameras(roverItem: RoverItemDto): RoverItemDto {
        roverItem.cameras.forEach { cameraItemDto ->
            insertCamera(Camera(roverName = roverItem.name, name = cameraItemDto.name, cameraFullName = cameraItemDto.fullName))
        }
        return roverItem
    }

    private suspend fun insertCamera(camera: Camera) = onIo {
        if (camerasDao.all().find { it.name == camera.name && it.roverName == camera.roverName } == null) camerasDao.insert(camera)
    }
}