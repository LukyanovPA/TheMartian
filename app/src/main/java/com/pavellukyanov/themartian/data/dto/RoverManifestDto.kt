package com.pavellukyanov.themartian.data.dto

import com.google.gson.annotations.SerializedName

class RoverManifestDto(
    @SerializedName("photo_manifest") val roverItem: RoverItemDto
)

class RoverItemDto(
    @SerializedName("name") val name: String,
    @SerializedName("landing_date") val landingDate: String,
    @SerializedName("launch_date") val launchDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("max_sol") val maxSol: Int,
    @SerializedName("max_date") val maxDate: String,
    @SerializedName("total_photos") val totalPhotos: Int,
    @SerializedName("photos") val roverManifestPhotos: List<RoverManifestPhotoDto>
)

class RoverManifestPhotoDto(
    @SerializedName("sol") val sol: Int,
    @SerializedName("earth_date") val earthDate: String,
    @SerializedName("total_photos") val totalPhotos: Int,
    @SerializedName("cameras") val cameras: List<String>
)

enum class RoverName(val roverName: String) {
    @SerializedName("roverName")
    PERSEVERANCE("Perseverance"),
    @SerializedName("roverName")
    CURIOSITY("Curiosity"),
    @SerializedName("roverName")
    OPPORTUNITY("Opportunity"),
    @SerializedName("roverName")
    SPIRIT("Spirit")
}