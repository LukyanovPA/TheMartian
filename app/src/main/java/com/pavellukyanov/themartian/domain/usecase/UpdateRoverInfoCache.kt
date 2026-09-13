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
    suspend operator fun invoke() = onIo {
        val rovers = apiDataSource.getRoversInfo()

        roverInfoDao.insert(rovers.map(RoverItemDto::toRover))
        insertMissingCameras(rovers)
    }

    private suspend fun insertMissingCameras(rovers: List<RoverItemDto>) = onIo {
        val cached = camerasDao.all().map { it.roverName to it.name }.toSet()

        val newCameras = rovers
            .flatMap { rover ->
                rover.cameras.map { camera ->
                    Camera(roverName = rover.name, name = camera.name, cameraFullName = camera.fullName)
                }
            }
            .distinctBy { it.roverName to it.name }
            .filter { (it.roverName to it.name) !in cached }

        if (newCameras.isNotEmpty()) camerasDao.insert(newCameras)
    }
}
