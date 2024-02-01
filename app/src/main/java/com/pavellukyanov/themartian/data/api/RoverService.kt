package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.data.dto.LatestDto
import com.pavellukyanov.themartian.data.dto.RoverManifestDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RoverService {
    @GET("manifests/{rover}/?")
    suspend fun loadRoverInfo(
        @Path("rover") roverName: String
    ): Response<RoverManifestDto>

    @GET("rovers/{rover}/latest_photos?")
    suspend fun getLatestPhotos(
        @Path("rover") roverName: String
    ): Response<LatestDto>
}