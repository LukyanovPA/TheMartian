package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.data.dto.LatestDto
import com.pavellukyanov.themartian.data.dto.PhotosDto
import com.pavellukyanov.themartian.data.dto.RoverManifestDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoverService {
    @GET("rovers/{rover}?include=cameras")
    suspend fun loadRoverInfo(
        @Path("rover") roverName: String
    ): Response<RoverManifestDto>

    @GET("photos")
    suspend fun getLatestPhotos(
        @Query("rovers") roverName: String,
        @Query("page") page: Int,
        @Query("sort") sort: String = "-earth_date",
        @Query("include") include: String = "rover,camera"
    ): Response<LatestDto>

    @GET("photos")
    suspend fun getByOptions(
        @Query("rovers") roverName: String,
        @Query("earth_date") earthDate: String,
        @Query("cameras") camera: String?,
        @Query("page") page: Int,
        @Query("include") include: String = "rover,camera"
    ): Response<PhotosDto>
}