package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.data.dto.RoverName

class ApiDataSource(private val roverService: RoverService) {

    suspend fun getRoversInfo(): List<RoverItemDto> =
        RoverName.entries
            .map { it.roverName }
            .map { name -> roverService.loadRoverInfo(roverName = name) }
            .map { it.toData().roverItem }

    suspend fun getLatestPhotos(roverName: String): List<PhotoDto> =
        roverService.getLatestPhotos(roverName = roverName, page = 1).toData().photoDtos

    suspend fun getPhotosByEarthDate(roverName: String, earthDate: String): List<PhotoDto> =
        roverService.getByEarthData(roverName = roverName, earthDate = earthDate).toData().photos
}