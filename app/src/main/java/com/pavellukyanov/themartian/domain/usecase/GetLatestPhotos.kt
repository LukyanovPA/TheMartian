package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.cache.dao.FavouritesDao
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.data.dto.map

class GetLatestPhotos(
    private val apiDataSource: ApiDataSource,
    private val favouritesDao: FavouritesDao
) {
    suspend operator fun invoke(roverName: String, isLocal: Boolean): List<Photo> =
        if (isLocal) favouritesDao.all()
        else apiDataSource.getLatestPhotos(roverName = roverName)
            .map(PhotoDto::map)
}