package com.pavellukyanov.themartian.data.dto

import com.google.gson.annotations.SerializedName

data class PhotosDto(
    @SerializedName("data") val photos: List<PhotoDto>
)
