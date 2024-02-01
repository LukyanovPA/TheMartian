package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.cache.dao.RoverInfoDao
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.domain.entity.toRover

class UpdateRoverInfoCache(
    private val roverInfoDao: RoverInfoDao,
    private val apiDataSource: ApiDataSource
) {
    suspend operator fun invoke() {
        roverInfoDao.insert(
            apiDataSource.getRoversInfo()
                .map(RoverItemDto::toRover)
        )
    }
}