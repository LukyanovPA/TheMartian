package com.pavellukyanov.themartian.domain.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pavellukyanov.themartian.data.dto.RoverItemDto
import com.pavellukyanov.themartian.data.dto.RoverName
import com.pavellukyanov.themartian.utils.DateFormatter

@Entity(tableName = "rover_info")
data class Rover(
    @PrimaryKey
    val roverName: String,
    val landingDate: String,
    val landingDateFormat: String,
    val launchDate: String,
    val status: String,
    val maxDate: String,
    val maxDateFormat: String,
    val totalPhotos: Int,
    val type: RoverName
)

fun RoverItemDto.toRover(): Rover = Rover(
    roverName = name,
    landingDate = landingDate,
    landingDateFormat = DateFormatter.format(landingDate),
    launchDate = DateFormatter.format(launchDate),
    status = status,
    maxDate = maxDate,
    maxDateFormat = DateFormatter.format(maxDate),
    totalPhotos = totalPhotos,
    type = RoverName.entries.find { it.roverName == name } ?: RoverName.CURIOSITY
)