package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.utils.ext.onCpu

class LoadPhotos(
    private val apiDataSource: ApiDataSource,
    private val updateCamerasCache: UpdateCamerasCache
) {
    suspend operator fun invoke(options: PhotosOptions, page: Int, isLatest: Boolean): List<Photo> {
        val photos = if (isLatest)
            apiDataSource.getLatest(
                roverName = options.roverName,
                page = page
            )
        else
            apiDataSource.getPhotosByOptions(
                options = options,
                page = page
            )

        onCpu { updateCamerasCache(photos = photos) }

        return photos
    }
}