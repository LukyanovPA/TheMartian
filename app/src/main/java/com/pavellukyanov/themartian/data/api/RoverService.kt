package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.data.dto.LatestDto
import com.pavellukyanov.themartian.data.dto.PhotosDto
import com.pavellukyanov.themartian.data.dto.RoverManifestDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoverService {
    @GET("rovers/{rover}/?")
    suspend fun loadRoverInfo(
        @Path("rover") roverName: String
    ): Response<RoverManifestDto>

    @GET("rovers/{rover}/latest_photos?")
    suspend fun getLatestPhotos(
        @Path("rover") roverName: String,
        @Query("page") page: Int
    ): Response<LatestDto>

    @GET("rovers/{rover}/photos?")
    suspend fun getByOptions(
        @Path("rover") roverName: String,
        @Query("earth_date") earthDate: String,
        @Query("camera") camera: String?,
        @Query("page") page: Int
    ): Response<PhotosDto>
}