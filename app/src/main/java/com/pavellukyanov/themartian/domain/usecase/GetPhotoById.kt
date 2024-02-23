package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.utils.ext.onMap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class GetPhotoById(
    private val photoDao: PhotoDao
) {
    operator fun invoke(id: Int): Flow<Photo?> =
        photoDao.observe()
            .flowOn(Dispatchers.IO)
            .onMap { list -> list.find { it.id == id } }
}