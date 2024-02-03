package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.FavouritesDao
import com.pavellukyanov.themartian.data.dto.Photo

class ChangeFavourites(
    private val favouritesDao: FavouritesDao
) {
    suspend fun add(photo: Photo) {
        favouritesDao.insert(photo)
    }

    suspend fun delete(photo: Photo) {
        favouritesDao.delete(photo)
    }
}