package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.utils.ext.onIo

data class PhotoLoadResult(
    val photos: List<Photo>,
    val canPaginate: Boolean,
    val totalPages: Int?,
    val currentPage: Int?,
    val perPage: Int,
    val totalCount: Int?
)

class LoadPhotos(
    private val apiDataSource: ApiDataSource
) {
    suspend operator fun invoke(options: PhotosOptions, page: Int, isLatest: Boolean): PhotoLoadResult = onIo {
        val result = apiDataSource.getPhotos(
            rovers = options.roverName.takeIf { it.isNotBlank() },
            earthDate = if (isLatest) null else options.date,
            camera = options.camera,
            page = page,
            perPage = options.perPage,
            sol = options.sol
        )

        PhotoLoadResult(
            photos = result.photos,
            canPaginate = result.canPaginate,
            totalPages = result.totalPages,
            currentPage = result.currentPage,
            perPage = result.perPage,
            totalCount = result.totalCount
        )
    }
}
