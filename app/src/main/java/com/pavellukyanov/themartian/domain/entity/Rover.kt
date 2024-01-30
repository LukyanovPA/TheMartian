package com.pavellukyanov.themartian.domain.entity

import androidx.annotation.DrawableRes
import com.pavellukyanov.themartian.R
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.data.dto.RoverName
import com.pavellukyanov.themartian.utils.DateFormatter

data class Rover(
    val roverName: String,
    val landingDate: String,
    val launchDate: String,
    val status: String,
    val maxDate: String,
    val totalPhotos: Int,
    val type: RoverName
) {
    @get:DrawableRes
    val roverImage: Int
        get() =
            when (type) {
                RoverName.PERSEVERANCE -> R.drawable.perseverance
                RoverName.CURIOSITY -> R.drawable.curiosity
                RoverName.OPPORTUNITY -> R.drawable.opportunity
                RoverName.SPIRIT -> R.drawable.spirit
            }
}

fun RoverItemDto.toRover(): Rover = Rover(
    roverName = name,
    landingDate = DateFormatter.format(landingDate),
    launchDate = DateFormatter.format(launchDate),
    status = status,
    maxDate = DateFormatter.format(maxDate),
    totalPhotos = totalPhotos,
    type = RoverName.entries.find { it.roverName == name } ?: RoverName.CURIOSITY
)