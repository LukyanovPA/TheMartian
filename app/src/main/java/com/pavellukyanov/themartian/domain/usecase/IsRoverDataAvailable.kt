package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.dto.RoverName
import com.pavellukyanov.themartian.domain.entity.Rover
import com.pavellukyanov.themartian.utils.ext.onCpu

class IsRoverDataAvailable {
    suspend operator fun invoke(rover: Rover): Boolean = onCpu {
        when (rover.roverName) {
            RoverName.OPPORTUNITY.roverName, RoverName.SPIRIT.roverName -> false
            else -> true
        }
    }
}