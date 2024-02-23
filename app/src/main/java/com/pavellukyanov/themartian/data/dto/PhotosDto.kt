package com.pavellukyanov.themartian.data.dto

import com.google.gson.annotations.SerializedName

data class PhotosDto(
    @SerializedName("photos") val photos: List<PhotoDto>
)
