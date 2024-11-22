package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.RoverInfoDao
import com.pavellukyanov.themartian.utils.ext.onIo

class IsEmptyRoverCache(private val roverInfoDao: RoverInfoDao) {
    suspend operator fun invoke(): Boolean = onIo { roverInfoDao.all().isEmpty() }
}