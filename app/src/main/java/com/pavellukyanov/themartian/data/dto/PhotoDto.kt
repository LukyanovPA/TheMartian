package com.pavellukyanov.themartian.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.pavellukyanov.themartian.utils.DateFormatter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotoDto(
    @SerialName("id") val id: Int,
    @SerialName("sol") val sol: Int,
    @SerialName("camera") val cameraDto: CameraDto,
    @SerialName("img_src") val imgSrc: String,
    @SerialName("earth_date") val earthDate: String,
    @SerialName("rover") val roverDto: RoverDto
)

@Entity(tableName = "photo")
data class Photo(
    @PrimaryKey
    val id: Int,
    val sol: Int,
    val cameraName: String,
    val cameraFullName: String,
    val earthDate: String,
    val roverName: String,
    val src: String,
    val isFavourites: Boolean = false,
    val isCache: Boolean = false
)

fun PhotoDto.map(): Photo =
    Photo(
        id = id,
        sol = sol,
        cameraName = cameraDto.name,
        cameraFullName = cameraDto.fullName,
        earthDate = DateFormatter.format(earthDate),
        roverName = roverDto.name,
        src = imgSrc
    )
