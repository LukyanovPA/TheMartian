package com.pavellukyanov.themartian.data.dto


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LatestDto(
    @SerialName("latest_photos") val photoDtos: List<PhotoDto>
)