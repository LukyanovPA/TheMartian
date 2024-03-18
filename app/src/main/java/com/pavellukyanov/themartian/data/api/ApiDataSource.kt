package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.common.NetworkMonitor
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.data.dto.RoverName
import com.pavellukyanov.themartian.data.dto.map
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.utils.ext.onIo

class ApiDataSource(
    private val roverService: RoverService,
    private val networkMonitor: NetworkMonitor
) {
    suspend fun getRoversInfo(): List<RoverItemDto> = networkMonitor {
        RoverName.entries
            .map { it.roverName }
            .map { name -> roverService.loadRoverInfo(roverName = name) }
            .map { it.toData().roverItem }
    }

    suspend fun getLatest(roverName: String, page: Int): List<Photo> = onIo {
        networkMonitor {
            roverService.getLatestPhotos(roverName = roverName, page = page)
                .toData()
                .photoDtos
                .map(PhotoDto::map)
        }
    }

    suspend fun getPhotosByOptions(options: PhotosOptions, page: Int): List<Photo> = onIo {
        networkMonitor {
            roverService.getByOptions(roverName = options.roverName, earthDate = options.date, camera = options.camera, page = page)
                .toData()
                .photos
                .map(PhotoDto::map)
        }
    }
}