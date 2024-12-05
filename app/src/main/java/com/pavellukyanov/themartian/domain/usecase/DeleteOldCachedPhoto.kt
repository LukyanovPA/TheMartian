package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.utils.ext.onIo

class DeleteOldCachedPhoto(
    private val photoDao: PhotoDao
) {
    suspend operator fun invoke() = onIo {
        photoDao.all()
            .filter { it.isCache && !it.isFavourites }
            .forEach(photoDao::delete)
    }
}