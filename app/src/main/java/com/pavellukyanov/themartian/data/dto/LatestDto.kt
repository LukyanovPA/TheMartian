package com.pavellukyanov.themartian.data.dto


import com.google.gson.annotations.SerializedName

data class LatestDto(
    @SerializedName("latest_photos") val photoDtos: List<PhotoDto>
)