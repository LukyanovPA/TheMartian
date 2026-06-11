package com.pavellukyanov.themartian.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pavellukyanov.themartian.utils.DateFormatter

// ============== Photo DTO ==============

data class PhotoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("type") val type: String? = null,
    @SerializedName("attributes") val attributes: PhotoAttributesDto,
    @SerializedName("relationships") val relationships: PhotoRelationshipsDto? = null,
    @SerializedName("meta") val photoMeta: PhotoMetaDto? = null
) {
    val sol: Int get() = attributes.sol
    val imgSrc: String
        get() = attributes.images?.medium
            ?: attributes.images?.small
            ?: attributes.images?.full
            ?: attributes.imgSrc
            ?: ""
    val earthDate: String get() = attributes.earthDate ?: ""
    val cameraDto: CameraDto
        get() = relationships?.camera?.let {
            CameraDto(
                id = 0,
                name = it.attributes?.name ?: it.id ?: "",
                roverId = 0,
                fullName = it.attributes?.fullName ?: ""
            )
        } ?: CameraDto(0, "", 0, "")
    val roverDto: RoverDto
        get() = relationships?.rover?.let {
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
    @SerializedName("nasa_id") val nasaId: String? = null,
    @SerializedName("sol") val sol: Int,
    @SerializedName("earth_date") val earthDate: String? = null,
    @SerializedName("date_taken_utc") val dateTakenUtc: String? = null,
    @SerializedName("date_taken_mars") val dateTakenMars: String? = null,
    @SerializedName("images") val images: ImagesDto? = null,
    @SerializedName("dimensions") val dimensions: DimensionsDto? = null,
    @SerializedName("sample_type") val sampleType: String? = null,
    @SerializedName("location") val location: LocationAttrDto? = null,
    @SerializedName("telemetry") val telemetry: TelemetryDto? = null,
    @SerializedName("title") val title: String? = null,
    @SerializedName("caption") val caption: String? = null,
    @SerializedName("credit") val credit: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("img_src") val imgSrc: String? = null,
    @SerializedName("raw_data") val rawData: Any? = null,
    @SerializedName("meta") val meta: PhotoMetaDto? = null
)

data class ImagesDto(
    @SerializedName("small") val small: String? = null,
    @SerializedName("medium") val medium: String? = null,
    @SerializedName("large") val large: String? = null,
    @SerializedName("full") val full: String? = null
)

data class DimensionsDto(
    @SerializedName("width") val width: Int? = null,
    @SerializedName("height") val height: Int? = null
)

data class LocationAttrDto(
    @SerializedName("site") val site: Int? = null,
    @SerializedName("drive") val drive: Int? = null,
    @SerializedName("coordinates") val coordinates: PhotoCoordinatesDto? = null
)

data class PhotoCoordinatesDto(
    @SerializedName("x") val x: Double? = null,
    @SerializedName("y") val y: Double? = null,
    @SerializedName("z") val z: Double? = null
)

data class TelemetryDto(
    @SerializedName("mast_azimuth") val mastAzimuth: Double? = null,
    @SerializedName("mast_elevation") val mastElevation: Double? = null,
    @SerializedName("spacecraft_clock") val spacecraftClock: Double? = null
)

data class PhotoMetaDto(
    @SerializedName("is_panorama_part") val isPanoramaPart: Boolean? = null,
    @SerializedName("panorama_sequence_id") val panoramaSequenceId: String? = null,
    @SerializedName("has_stereo_pair") val hasStereoPair: Boolean? = null,
    @SerializedName("stereo_pair_id") val stereoPairId: Int? = null,
    @SerializedName("lighting_conditions") val lightingConditions: String? = null,
    @SerializedName("location_visits") val locationVisits: Int? = null
)

// ============== Relationships ==============

class PhotoRelationshipsDto(
    @SerializedName("rover") val rover: ResourceReferenceDto?,
    @SerializedName("camera") val camera: ResourceReferenceDto?
)

class ResourceReferenceDto(
    @SerializedName("id") val id: String?,
    @SerializedName("type") val type: String? = null,
    @SerializedName("attributes") val attributes: ResourceAttributesDto? = null
)

class ResourceAttributesDto(
    @SerializedName("name") val name: String?,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("photo_count") val photoCount: Int? = null
)

// ============== Rover Manifest ==============

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
    @SerializedName("id") val id: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("attributes") val attributes: RoverAttributesDto,
    @SerializedName("relationships") val relationships: RoverRelationshipsDto? = null
)

// ============== Rover ==============

class RoverAttributesDto(
    @SerializedName("name") val name: String? = null,
    @SerializedName("landing_date") val landingDate: String? = null,
    @SerializedName("launch_date") val launchDate: String? = null,
    @SerializedName("status") val status: String? = null,
    @SerializedName("max_sol") val maxSol: Int? = null,
    @SerializedName("max_date") val maxDate: String? = null,
    @SerializedName("total_photos") val totalPhotos: Int? = null
)

class RoverRelationshipsDto(
    @SerializedName("cameras") val cameras: List<CameraResourceDto>? = null
)

// ============== Camera ==============

class CameraResourceDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("type") val type: String? = null,
    @SerializedName("attributes") val attributes: CameraItemDto
)

data class CameraItemDto(
    @SerializedName("name") val name: String = "",
    @SerializedName("full_name") val fullName: String = "",
    @SerializedName("photo_count") val photoCount: Int? = null,
    @SerializedName("first_photo_sol") val firstPhotoSol: Int? = null,
    @SerializedName("last_photo_sol") val lastPhotoSol: Int? = null
)

// ============== RoverItem (used domain-side) ==============

data class RoverItemDto(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("landing_date") val landingDate: String = "",
    @SerializedName("launch_date") val launchDate: String = "",
    @SerializedName("status") val status: String = "",
    @SerializedName("max_sol") val maxSol: Int = 0,
    @SerializedName("max_date") val maxDate: String = "",
    @SerializedName("total_photos") val totalPhotos: Int = 0,
    @SerializedName("cameras") val cameras: List<CameraItemDto> = emptyList()
)

// ============== RoverName ==============

enum class RoverName(val roverName: String) {
    @SerializedName("roverName") PERSEVERANCE("Perseverance"),
    @SerializedName("roverName") CURIOSITY("Curiosity"),
    @SerializedName("roverName") OPPORTUNITY("Opportunity"),
    @SerializedName("roverName") SPIRIT("Spirit")
}

// ============== Flattened helpers ==============

data class CameraDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("rover_id") val roverId: Int,
    @SerializedName("full_name") val fullName: String
)

data class RoverDto(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("landing_date") val landingDate: String,
    @SerializedName("launch_date") val launchDate: String,
    @SerializedName("status") val status: String
)

// ============== Room entity ==============

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
    val isCache: Boolean = false,
    val srcSmall: String? = null,
    val srcMedium: String? = null,
    val srcLarge: String? = null,
    val srcFull: String? = null,
    val isPanoramaPart: Boolean = false,
    val panoramaSequenceId: String? = null,
    val hasStereoPair: Boolean = false,
    val stereoPairId: Int? = null,
    val lightingConditions: String? = null,
    val locationVisits: Int? = null
)

// ============== Mapping ==============

fun PhotoDto.map(): Photo =
    Photo(
        id = id,
        sol = sol,
        cameraName = cameraDto.name,
        cameraFullName = cameraDto.fullName,
        earthFormattedDate = DateFormatter.format(earthDate),
        earthDate = earthDate,
        roverName = roverDto.name,
        src = imgSrc,
        srcSmall = attributes.images?.small,
        srcMedium = attributes.images?.medium,
        srcLarge = attributes.images?.large,
        srcFull = attributes.images?.full,
        isPanoramaPart = photoMeta?.isPanoramaPart ?: attributes.meta?.isPanoramaPart ?: false,
        panoramaSequenceId = photoMeta?.panoramaSequenceId ?: attributes.meta?.panoramaSequenceId,
        hasStereoPair = photoMeta?.hasStereoPair ?: attributes.meta?.hasStereoPair ?: false,
        stereoPairId = photoMeta?.stereoPairId ?: attributes.meta?.stereoPairId,
        lightingConditions = photoMeta?.lightingConditions ?: attributes.meta?.lightingConditions,
        locationVisits = photoMeta?.locationVisits ?: attributes.meta?.locationVisits
    )
