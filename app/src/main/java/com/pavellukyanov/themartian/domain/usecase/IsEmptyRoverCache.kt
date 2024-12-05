package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.RoverInfoDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

class IsEmptyRoverCache(private val roverInfoDao: RoverInfoDao) {
    operator fun invoke(): Flow<Boolean> =
        roverInfoDao.observe()
            .map { it.isEmpty() }
            .flowOn(Dispatchers.IO)
}