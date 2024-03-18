package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.RoverInfoDao

class IsEmptyRoverCache(private val roverInfoDao: RoverInfoDao) {
    suspend operator fun invoke(): Boolean = roverInfoDao.all().isEmpty()
}