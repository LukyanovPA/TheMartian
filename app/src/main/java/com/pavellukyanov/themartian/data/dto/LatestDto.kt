package com.pavellukyanov.themartian.data.dto


import com.google.gson.annotations.SerializedName

data class LatestDto(
    @SerializedName("data") val photoDtos: List<PhotoDto>
)