package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.utils.ext.onIo

class GetRoversOnFavourites(
    private val photoDao: PhotoDao
) {
    suspend operator fun invoke(): List<String> = onIo {
        mutableSetOf<String>().apply {
            addAll(
                photoDao.all()
                    .filter { it.isFavourites }
                    .map { it.roverName }
            )
        }.toList()
    }
}