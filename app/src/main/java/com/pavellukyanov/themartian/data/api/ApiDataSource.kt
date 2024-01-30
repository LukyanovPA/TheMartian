package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.data.dto.RoverName
import com.pavellukyanov.themartian.data.dto.toData

class ApiDataSource(private val roverService: RoverService) {

    suspend fun getRoversInfo(): List<RoverItemDto> =
        RoverName.entries
            .map { it.roverName }
            .map { name -> roverService.loadRoverInfo(roverName = name) }
            .map { it.toData().roverItem }
}