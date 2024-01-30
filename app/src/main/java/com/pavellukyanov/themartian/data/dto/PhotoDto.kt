package com.pavellukyanov.themartian.data.dto

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
