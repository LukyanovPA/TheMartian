package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.data.dto.LatestDto
import com.pavellukyanov.themartian.data.dto.PhotosDto
import com.pavellukyanov.themartian.data.dto.RoverManifestDto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoverService {
    @GET("manifests/{rover}/?")
    fun loadRoverInfo(
        @Path("rover") roverName: String
    ): Call<RoverManifestDto>

    @GET("rovers/{rover}/latest_photos?")
    fun getLatestPhotos(
        @Path("rover") roverName: String,
        @Query("page") page: Int
    ): Call<LatestDto>

    @GET("rovers/{rover}/photos?")
    fun getByOptions(
        @Path("rover") roverName: String,
        @Query("earth_date") earthDate: String,
        @Query("camera") camera: String?,
        @Query("page") page: Int
    ): Call<PhotosDto>
}