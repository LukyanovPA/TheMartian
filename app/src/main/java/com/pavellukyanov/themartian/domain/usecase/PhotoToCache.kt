package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.utils.ext.onIo

class PhotoToCache(
    private val photoDao: PhotoDao
) {
    suspend operator fun invoke(photo: Photo) = onIo {
        val cache = photoDao.getById(id = photo.id)
        photoDao.insert(cache?.copy(isCache = true) ?: photo.copy(isCache = true))
    }
}