package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.RoverInfoDao
import com.pavellukyanov.themartian.utils.ext.onIo

class DeleteRoverInfoCache(
    private val roverInfoDao: RoverInfoDao
) {
    suspend operator fun invoke() = onIo {
        roverInfoDao.deleteAll()
    }
}