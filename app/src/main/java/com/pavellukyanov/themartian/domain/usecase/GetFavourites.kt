package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.utils.ext.onMap
import kotlinx.coroutines.flow.Flow

class GetFavourites(
    private val photoDao: PhotoDao
) {
    operator fun invoke(roverName: String): Flow<List<Photo>> =
        photoDao.observe()
            .onMap { list -> list.filter { it.isFavourites } }
            .onMap { list -> list.filter { it.roverName.contains(roverName, ignoreCase = true) } }
}