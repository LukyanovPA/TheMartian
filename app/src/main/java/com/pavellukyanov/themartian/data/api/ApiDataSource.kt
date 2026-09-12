package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.common.NetworkMonitor
import com.pavellukyanov.themartian.data.dto.CameraItemDto
import com.pavellukyanov.themartian.data.dto.CameraResourceDto
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.data.dto.RoverDataDto
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.data.dto.map
import com.pavellukyanov.themartian.utils.ext.onIo
import com.pavellukyanov.themartian.utils.ext.toData

class ApiDataSource(
    private val roverService: RoverService,
    private val networkMonitor: NetworkMonitor
) {

    data class PhotoPageResult(
        val photos: List<Photo>,
        val canPaginate: Boolean,
        val totalPages: Int?,
        val currentPage: Int?,
        val perPage: Int,
        val totalCount: Int?
    )

    suspend fun getPhotos(
        rovers: String?,
        earthDate: String?,
        camera: String?,
        page: Int,
        perPage: Int = 25,
        sol: Int? = null,
        imageSizes: String = "small,medium,large,full"
    ): PhotoPageResult = onIo {
        networkMonitor {
            val response = roverService.getPhotos(
                rovers = rovers,
                cameras = camera?.takeIf { it.isNotBlank() },
                earthDate = earthDate,
                sol = sol,
                page = page,
                perPage = perPage,
                imageSizes = imageSizes
            ).toData()

            val photos = response.data.map(PhotoDto::map)
            val pagination = response.pagination
            val totalPages = pagination?.totalPages
            val currentPage = pagination?.page ?: page
            val canPaginate = totalPages != null && currentPage < totalPages

            PhotoPageResult(
                photos = photos,
                canPaginate = canPaginate,
                totalPages = totalPages,
                currentPage = currentPage,
                perPage = pagination?.perPage ?: perPage,
                totalCount = response.meta?.totalCount
            )
        }
    }

    suspend fun getPhoto(id: Int): Photo? = onIo {
        try {
            val response = roverService.getPhoto(id = id)
            if (response.isSuccessful) response.body()?.data?.map()
            else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getRoversInfo(): List<RoverItemDto> = onIo {
        networkMonitor {
            val rovers = roverService.getRovers().toData().data
            val camerasByRover = roverService.getAllCameras().toData().data
                .groupBy { it.relationships?.rover?.id.orEmpty() }
                .mapValues { (_, cameras) -> cameras.map(CameraResourceDto::attributes) }

            rovers.map { rover ->
                rover.toRoverItemDto(cameras = camerasByRover[rover.id.orEmpty()].orEmpty())
            }
        }
    }
}

private fun RoverDataDto.toRoverItemDto(cameras: List<CameraItemDto>): RoverItemDto =
    RoverItemDto(
        id = 0,
        name = attributes.name ?: "",
        landingDate = attributes.landingDate ?: "",
        launchDate = attributes.launchDate ?: "",
        status = attributes.status ?: "",
        maxSol = attributes.maxSol ?: 0,
        maxDate = attributes.maxDate ?: "",
        totalPhotos = attributes.totalPhotos ?: 0,
        cameras = cameras
    )
