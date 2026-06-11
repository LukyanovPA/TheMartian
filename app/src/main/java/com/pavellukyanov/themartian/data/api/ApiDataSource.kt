package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.common.NetworkMonitor
import com.pavellukyanov.themartian.data.dto.ApiResponse
import com.pavellukyanov.themartian.data.dto.CameraItemDto
import com.pavellukyanov.themartian.data.dto.CameraResourceDto
import com.pavellukyanov.themartian.data.dto.LocationResourceDto
import com.pavellukyanov.themartian.data.dto.PanoramaResourceDto
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.data.dto.RoverName
import com.pavellukyanov.themartian.data.dto.StatsResourceDto
import com.pavellukyanov.themartian.data.dto.map
import com.pavellukyanov.themartian.utils.ext.onIo
import com.pavellukyanov.themartian.utils.ext.toData
import com.pavellukyanov.themartian.utils.ext.toDataOrNull

class ApiDataSource(
    private val roverService: RoverService,
    private val networkMonitor: NetworkMonitor
) {
    // ============== Photos ==============

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

    // ============== Rovers ==============

    suspend fun getRoversInfo(): List<RoverItemDto> = onIo {
        networkMonitor {
            val response = roverService.getRovers().toData()
            response.data.map { data ->
                RoverItemDto(
                    id = 0,
                    name = data.attributes.name ?: "",
                    landingDate = data.attributes.landingDate ?: "",
                    launchDate = data.attributes.launchDate ?: "",
                    status = data.attributes.status ?: "",
                    maxSol = data.attributes.maxSol ?: 0,
                    maxDate = data.attributes.maxDate ?: "",
                    totalPhotos = data.attributes.totalPhotos ?: 0,
                    cameras = emptyList()
                )
            }
        }
    }

    suspend fun getRoverInfoBySlug(slug: String): RoverItemDto? = onIo {
        try {
            val response = roverService.getRover(slug = slug)
            if (response.isSuccessful) {
                response.body()?.data?.let { data ->
                    RoverItemDto(
                        id = 0,
                        name = data.attributes.name ?: "",
                        landingDate = data.attributes.landingDate ?: "",
                        launchDate = data.attributes.launchDate ?: "",
                        status = data.attributes.status ?: "",
                        maxSol = data.attributes.maxSol ?: 0,
                        maxDate = data.attributes.maxDate ?: "",
                        totalPhotos = data.attributes.totalPhotos ?: 0,
                        cameras = data.relationships?.cameras?.map { it.attributes } ?: emptyList()
                    )
                }
            } else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun getRoverCameras(slug: String): List<CameraItemDto> = onIo {
        networkMonitor {
            val response: ApiResponse<List<CameraResourceDto>> = roverService.getRoverCameras(slug = slug).toData()
            response.data.map { it.attributes }
        }
    }

    // ============== Panoramas ==============

    suspend fun getPanoramas(
        rovers: String? = null,
        solMin: Int? = null,
        solMax: Int? = null,
        minPhotos: Int? = null
    ): List<PanoramaResourceDto> = onIo {
        networkMonitor {
            roverService.getPanoramas(
                rovers = rovers,
                solMin = solMin,
                solMax = solMax,
                minPhotos = minPhotos
            ).toData().data
        }
    }

    // ============== Locations ==============

    suspend fun getLocations(
        rovers: String? = null,
        solMin: Int? = null,
        solMax: Int? = null,
        minPhotos: Int? = null
    ): List<LocationResourceDto> = onIo {
        networkMonitor {
            roverService.getLocations(
                rovers = rovers,
                solMin = solMin,
                solMax = solMax,
                minPhotos = minPhotos
            ).toData().data
        }
    }

    // ============== Stats ==============

    suspend fun getPhotoStats(
        groupBy: String? = null,
        rovers: String? = null
    ): List<StatsResourceDto> = onIo {
        networkMonitor {
            roverService.getPhotoStats(groupBy = groupBy, rovers = rovers)
                .toData().data
        }
    }
}
