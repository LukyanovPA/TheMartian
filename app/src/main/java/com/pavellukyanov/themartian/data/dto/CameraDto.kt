package com.pavellukyanov.themartian.data.dto

import com.google.gson.annotations.SerializedName

data class CameraDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("rover_id") val roverId: Int,
    @SerializedName("full_name") val fullName: String
)