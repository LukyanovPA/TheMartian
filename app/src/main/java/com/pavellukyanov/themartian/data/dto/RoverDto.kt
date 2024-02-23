package com.pavellukyanov.themartian.data.dto


import com.google.gson.annotations.SerializedName

data class RoverDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("landing_date") val landingDate: String,
    @SerializedName("launch_date") val launchDate: String,
    @SerializedName("status") val status: String
)