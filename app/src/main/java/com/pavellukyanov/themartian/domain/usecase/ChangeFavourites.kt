package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.FavouritesDao
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.utils.ext.onIo

class ChangeFavourites(
    private val favouritesDao: FavouritesDao
) {
    suspend fun add(photo: Photo) = onIo {
        favouritesDao.insert(photo)
    }

    suspend fun delete(photo: Photo) = onIo {
        favouritesDao.delete(photo)
    }
}