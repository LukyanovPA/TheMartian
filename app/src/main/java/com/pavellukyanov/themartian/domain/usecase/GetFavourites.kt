package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.data.dto.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetFavourites(
    private val photoDao: PhotoDao
) {
    operator fun invoke(roverName: String): Flow<List<Photo>> =
        photoDao.observe()
            .map { list -> list.filter { it.isFavourites } }
            .map { list -> list.filter { it.roverName.contains(roverName, ignoreCase = true) } }
}