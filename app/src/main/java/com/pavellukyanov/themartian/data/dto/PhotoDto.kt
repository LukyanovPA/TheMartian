package com.pavellukyanov.themartian.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pavellukyanov.themartian.utils.DateFormatter

data class PhotoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("attributes") val attributes: PhotoAttributesDto,
    @SerializedName("relationships") val relationships: PhotoRelationshipsDto?
) {
    val sol: Int get() = attributes.sol
    val imgSrc: String get() = attributes.imgSrc ?: ""
    val earthDate: String get() = attributes.earthDate ?: ""
    val cameraDto: CameraDto get() = relationships?.camera?.let { 
        CameraDto(
            id = 0, 
            name = it.attributes?.name ?: it.id ?: "", 
            roverId = 0, 
            fullName = it.attributes?.fullName ?: ""
        ) 
    } ?: CameraDto(0, "", 0, "")
    val roverDto: RoverDto get() = relationships?.rover?.let { 
        RoverDto(
            id = 0, 
            name = it.attributes?.name ?: it.id ?: "", 
            landingDate = "", 
            launchDate = "", 
            status = ""
        ) 
    } ?: RoverDto(0, "", "", "", "")
}

class PhotoAttributesDto(
    @SerializedName("sol") val sol: Int,
    @SerializedName("img_src") val imgSrc: String?,
    @SerializedName("earth_date") val earthDate: String?
)

class PhotoRelationshipsDto(
    @SerializedName("rover") val rover: ResourceReferenceDto?,
    @SerializedName("camera") val camera: ResourceReferenceDto?
)

class ResourceReferenceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("attributes") val attributes: ResourceAttributesDto?
)

class ResourceAttributesDto(
    @SerializedName("name") val name: String?,
    @SerializedName("full_name") val fullName: String?
)

@Entity(tableName = "photo")
data class Photo(
    @PrimaryKey
    val id: Int,
    val sol: Int,
    val cameraName: String,
    val cameraFullName: String,
    val earthFormattedDate: String,
    val earthDate: String,
    val roverName: String,
    val src: String,
    val isFavourites: Boolean = false,
    val isCache: Boolean = false
)

fun PhotoDto.map(): Photo =
    Photo(
        id = id,
        sol = sol,
        cameraName = cameraDto.name,
        cameraFullName = cameraDto.fullName,
        earthFormattedDate = DateFormatter.format(earthDate),
        earthDate = earthDate,
        roverName = roverDto.name,
        src = imgSrc
    )
