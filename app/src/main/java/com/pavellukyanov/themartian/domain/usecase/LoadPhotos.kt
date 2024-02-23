package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.utils.ext.onCpu
import com.pavellukyanov.themartian.utils.ext.onIo

class LoadPhotos(
    private val apiDataSource: ApiDataSource,
    private val updateCamerasCache: UpdateCamerasCache
) {
    suspend operator fun invoke(options: PhotosOptions, page: Int, isLatest: Boolean): List<Photo> = onIo {
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

        photos.also {
            onCpu { updateCamerasCache(photos = photos) }
        }
    }
}