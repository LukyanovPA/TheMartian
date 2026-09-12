package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.data.dto.ApiResponse
import com.pavellukyanov.themartian.data.dto.CameraResourceDto
import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.data.dto.RoverDataDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RoverService {

    @GET("photos")
    suspend fun getPhotos(
        @Query("rovers") rovers: String? = null,
        @Query("cameras") cameras: String? = null,
        @Query("sol") sol: Int? = null,
        @Query("sol_min") solMin: Int? = null,
        @Query("sol_max") solMax: Int? = null,
        @Query("earth_date") earthDate: String? = null,
        @Query("date_min") dateMin: String? = null,
        @Query("date_max") dateMax: String? = null,
        @Query("sort") sort: String? = "-earth_date",
        @Query("include") include: String = "rover,camera",
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 25,
        @Query("image_sizes") imageSizes: String? = null,
        @Query("sample_type") sampleType: String? = null,
        @Query("nasa_id") nasaId: String? = null
    ): Response<ApiResponse<List<PhotoDto>>>

    @GET("photos/{id}")
    suspend fun getPhoto(
        @Path("id") id: Int,
        @Query("include") include: String = "rover,camera"
    ): Response<ApiResponse<PhotoDto>>

    @GET("rovers")
    suspend fun getRovers(): Response<ApiResponse<List<RoverDataDto>>>

    @GET("cameras")
    suspend fun getAllCameras(
        @Query("rover") rover: String? = null
    ): Response<ApiResponse<List<CameraResourceDto>>>
}
