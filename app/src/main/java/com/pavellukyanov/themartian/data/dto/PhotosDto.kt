package com.pavellukyanov.themartian.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PhotosDto(
    @SerialName("photos")
    val photos: List<PhotoDto>
)
