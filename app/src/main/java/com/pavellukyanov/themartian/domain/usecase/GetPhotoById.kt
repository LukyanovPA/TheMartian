package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.data.dto.Photo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class GetPhotoById(
    private val photoDao: PhotoDao
) {
    operator fun invoke(id: Int): Flow<Photo?> =
        photoDao.observe()
            .map { list ->
                list.find { it.id == id }
            }.flowOn(Dispatchers.IO)
}