package com.pavellukyanov.themartian.data.dto

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("data") val data: T,
    @SerializedName("meta") val meta: ResponseMetaDto? = null,
    @SerializedName("pagination") val pagination: PaginationDto? = null,
    @SerializedName("links") val links: LinksDto? = null
)

data class ResponseMetaDto(
    @SerializedName("total_count") val totalCount: Int? = null,
    @SerializedName("returned_count") val returnedCount: Int = 0,
    @SerializedName("query") val query: Any? = null,
    @SerializedName("timestamp") val timestamp: String? = null
)

data class PaginationDto(
    @SerializedName("page") val page: Int? = null,
    @SerializedName("per_page") val perPage: Int = 25,
    @SerializedName("total_pages") val totalPages: Int? = null,
    @SerializedName("cursor") val cursor: CursorDto? = null
)

data class CursorDto(
    @SerializedName("current") val current: String? = null,
    @SerializedName("next") val next: String? = null,
    @SerializedName("previous") val previous: String? = null
)

data class LinksDto(
    @SerializedName("self") val self: String? = null,
    @SerializedName("next") val next: String? = null,
    @SerializedName("prev") val previous: String? = null,
    @SerializedName("first") val first: String? = null,
    @SerializedName("last") val last: String? = null
)
