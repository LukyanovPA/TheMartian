package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.utils.ext.onIo

class GetPhotoFromApi(
    private val apiDataSource: ApiDataSource,
    private val photoDao: PhotoDao
) {
    suspend operator fun invoke(id: Int): com.pavellukyanov.themartian.data.dto.Photo? = onIo {
        try {
            val result = apiDataSource.getPhoto(id)
            if (result != null && result.src.isNotBlank()) {
                val cached = photoDao.getById(id)
                if (cached != null) {
                    val updated = cached.copy(
                        srcLarge = result.srcLarge ?: cached.srcLarge,
                        srcFull = result.srcFull ?: cached.srcFull,
                        src = result.src
                    )
                    photoDao.insert(updated)
                }
                result
            } else result
        } catch (_: Exception) {
            null
        }
    }
}
