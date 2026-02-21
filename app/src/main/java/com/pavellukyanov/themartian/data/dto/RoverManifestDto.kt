package com.pavellukyanov.themartian.data.dto

import com.google.gson.annotations.SerializedName

class RoverManifestDto(
    @SerializedName("data") val data: RoverDataDto
) {
    val roverItem: RoverItemDto
        get() = RoverItemDto(
            id = 0,
            name = data.attributes.name ?: "",
            landingDate = data.attributes.landingDate ?: "",
            launchDate = data.attributes.launchDate ?: "",
            status = data.attributes.status ?: "",
            maxSol = data.attributes.maxSol ?: 0,
            maxDate = data.attributes.maxDate ?: "",
            totalPhotos = data.attributes.totalPhotos ?: 0,
            cameras = data.relationships?.cameras?.map { it.attributes } ?: emptyList()
        )
}

class RoverDataDto(
    @SerializedName("id") val id: String,
    @SerializedName("attributes") val attributes: RoverAttributesDto,
    @SerializedName("relationships") val relationships: RoverRelationshipsDto?
)

class RoverAttributesDto(
    @SerializedName("name") val name: String?,
    @SerializedName("landing_date") val landingDate: String?,
    @SerializedName("launch_date") val launchDate: String?,
    @SerializedName("status") val status: String?,
    @SerializedName("max_sol") val maxSol: Int?,
    @SerializedName("max_date") val maxDate: String?,
    @SerializedName("total_photos") val totalPhotos: Int?
)

class RoverRelationshipsDto(
    @SerializedName("cameras") val cameras: List<CameraResourceDto>?
)

class CameraResourceDto(
    @SerializedName("attributes") val attributes: CameraItemDto
)

class RoverItemDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("landing_date") val landingDate: String,
    @SerializedName("launch_date") val launchDate: String,
    @SerializedName("status") val status: String,
    @SerializedName("max_sol") val maxSol: Int,
    @SerializedName("max_date") val maxDate: String,
    @SerializedName("total_photos") val totalPhotos: Int,
    @SerializedName("cameras") val cameras: List<CameraItemDto>
)

data class CameraItemDto(
    @SerializedName("name") val name: String,
    @SerializedName("full_name") val fullName: String
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