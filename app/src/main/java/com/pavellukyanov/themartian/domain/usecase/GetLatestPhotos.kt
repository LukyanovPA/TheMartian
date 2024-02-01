package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.dto.PhotoDto

class GetLatestPhotos(
    private val apiDataSource: ApiDataSource
) {
    suspend operator fun invoke(roverName: String): List<PhotoDto> =
        apiDataSource.getLatestPhotos(roverName = roverName)
}