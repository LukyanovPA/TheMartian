package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.utils.ext.onIo

class ChangeFavourites(
    private val photoDao: PhotoDao
) {
    suspend fun add(photo: Photo) = onIo {
        photoDao.insert(photo.copy(isFavourites = true))
    }

    suspend fun delete(photo: Photo) = onIo {
        photoDao.insert(photo.copy(isFavourites = false))
    }
}