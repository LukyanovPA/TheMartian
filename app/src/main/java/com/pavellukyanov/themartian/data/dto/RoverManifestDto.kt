package com.pavellukyanov.themartian.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class RoverManifestDto(
    @SerialName("photo_manifest") val roverItem: RoverItemDto
)

@Serializable
class RoverItemDto(
    @SerialName("name") val name: String,
    @SerialName("landing_date") val landingDate: String,
    @SerialName("launch_date") val launchDate: String,
    @SerialName("status") val status: String,
    @SerialName("max_sol") val maxSol: Int,
    @SerialName("max_date") val maxDate: String,
    @SerialName("total_photos") val totalPhotos: Int,
    @SerialName("photos") val roverManifestPhotos: List<RoverManifestPhotoDto>
)

@Serializable
class RoverManifestPhotoDto(
    @SerialName("sol") val sol: Int,
    @SerialName("earth_date") val earthDate: String,
    @SerialName("total_photos") val totalPhotos: Int,
    @SerialName("cameras") val cameras: List<String>
)

enum class RoverName(val roverName: String) {
    PERSEVERANCE("Perseverance"),
    CURIOSITY("Curiosity"),
    OPPORTUNITY("Opportunity"),
    SPIRIT("Spirit")
}