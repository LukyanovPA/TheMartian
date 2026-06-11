package com.pavellukyanov.themartian.data.api

import com.pavellukyanov.themartian.data.dto.ApiResponse
import com.pavellukyanov.themartian.data.dto.CameraResourceDto
import com.pavellukyanov.themartian.data.dto.JourneyPointDto
import com.pavellukyanov.themartian.data.dto.LocationResourceDto
import com.pavellukyanov.themartian.data.dto.PanoramaResourceDto
import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.data.dto.RoverDataDto
import com.pavellukyanov.themartian.data.dto.RoverManifestDto
import com.pavellukyanov.themartian.data.dto.StatsResourceDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

data class BatchIdsRequest(val ids: List<Int>)

interface RoverService {
    // ============ PHOTOS ============

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

    @GET("photos/stats")
    suspend fun getPhotoStats(
        @Query("group_by") groupBy: String? = null,
        @Query("rovers") rovers: String? = null
    ): Response<ApiResponse<List<StatsResourceDto>>>

    @POST("photos/batch")
    suspend fun getPhotosBatch(
        @Body body: BatchIdsRequest
    ): Response<ApiResponse<List<PhotoDto>>>

    // ============ ROVERS ============

    @GET("rovers")
    suspend fun getRovers(): Response<ApiResponse<List<RoverDataDto>>>

    @GET("rovers/{slug}")
    suspend fun getRover(
        @Path("slug") slug: String,
        @Query("include") include: String? = "cameras"
    ): Response<ApiResponse<RoverDataDto>>

    @GET("rovers/{slug}/manifest")
    suspend fun getRoverManifest(
        @Path("slug") slug: String
    ): Response<RoverManifestDto>

    @GET("rovers/{slug}/cameras")
    suspend fun getRoverCameras(
        @Path("slug") slug: String
    ): Response<ApiResponse<List<CameraResourceDto>>>

    @GET("rovers/{slug}/journey")
    suspend fun getRoverJourney(
        @Path("slug") slug: String
    ): Response<ApiResponse<List<JourneyPointDto>>>

    @GET("rovers/{slug}/traverse")
    suspend fun getRoverTraverse(
        @Path("slug") slug: String,
        @Query("sol_min") solMin: Int? = null,
        @Query("sol_max") solMax: Int? = null,
        @Query("format") format: String? = "json",
        @Query("simplify") simplify: Float? = null,
        @Query("include_segments") includeSegments: Boolean? = null
    ): Response<ApiResponse<List<JourneyPointDto>>>

    // ============ CAMERAS ============

    @GET("cameras")
    suspend fun getAllCameras(
        @Query("rover") rover: String? = null
    ): Response<ApiResponse<List<CameraResourceDto>>>

    @GET("cameras/{id}")
    suspend fun getCamera(
        @Path("id") id: String,
        @Query("rover") rover: String? = null
    ): Response<ApiResponse<CameraResourceDto>>

    // ============ LOCATIONS ============

    @GET("locations")
    suspend fun getLocations(
        @Query("rovers") rovers: String? = null,
        @Query("sol_min") solMin: Int? = null,
        @Query("sol_max") solMax: Int? = null,
        @Query("min_photos") minPhotos: Int? = null
    ): Response<ApiResponse<List<LocationResourceDto>>>

    @GET("locations/{id}")
    suspend fun getLocation(
        @Path("id") id: String
    ): Response<ApiResponse<LocationResourceDto>>

    // ============ PANORAMAS ============

    @GET("panoramas")
    suspend fun getPanoramas(
        @Query("rovers") rovers: String? = null,
        @Query("sol_min") solMin: Int? = null,
        @Query("sol_max") solMax: Int? = null,
        @Query("min_photos") minPhotos: Int? = null
    ): Response<ApiResponse<List<PanoramaResourceDto>>>

    @GET("panoramas/{id}")
    suspend fun getPanorama(
        @Path("id") id: String
    ): Response<ApiResponse<PanoramaResourceDto>>
}
