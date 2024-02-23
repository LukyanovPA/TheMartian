package com.pavellukyanov.themartian.data.dto

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.pavellukyanov.themartian.utils.DateFormatter

data class PhotoDto(
    @SerializedName("id") val id: Int,
    @SerializedName("sol") val sol: Int,
    @SerializedName("camera") val cameraDto: CameraDto,
    @SerializedName("img_src") val imgSrc: String,
    @SerializedName("earth_date") val earthDate: String,
    @SerializedName("rover") val roverDto: RoverDto
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
