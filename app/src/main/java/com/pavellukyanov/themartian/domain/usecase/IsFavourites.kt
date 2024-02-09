package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.FavouritesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class IsFavourites(
    private val favouritesDao: FavouritesDao
) {
    operator fun invoke(id: Int): Flow<Boolean> =
        favouritesDao.observe()
            .map { list ->
                list.find { it.id == id } != null
            }.flowOn(Dispatchers.IO)
}