package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.domain.entity.Rover
import com.pavellukyanov.themartian.domain.entity.toRover

class LoadRovers(private val apiDataSource: ApiDataSource) {
    suspend operator fun invoke(): List<Rover> =
        apiDataSource.getRoversInfo()
            .map(RoverItemDto::toRover)
}