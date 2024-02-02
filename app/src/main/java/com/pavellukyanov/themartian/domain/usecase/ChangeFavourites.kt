package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.FavouritesDao
import com.pavellukyanov.themartian.data.dto.PhotoDto

class ChangeFavourites(
    private val favouritesDao: FavouritesDao
) {
    suspend fun add(photo: PhotoDto) {
        favouritesDao.insert(photo)
    }

    suspend fun delete(photo: PhotoDto) {
        favouritesDao.delete(photo)
    }
}