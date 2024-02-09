package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.cache.dao.RoverInfoDao
import com.pavellukyanov.themartian.domain.entity.Rover
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn

class LoadRovers(private val roverInfoDao: RoverInfoDao) {
    operator fun invoke(): Flow<List<Rover>> =
        roverInfoDao
            .subscribeAll()
            .flowOn(Dispatchers.IO)
}