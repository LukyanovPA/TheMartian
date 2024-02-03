package com.pavellukyanov.themartian.domain.usecase

import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.data.dto.PhotoDto
import com.pavellukyanov.themartian.data.dto.map

class GetPhotosByEarthDateRemote(private val apiDataSource: ApiDataSource) {
    suspend operator fun invoke(roverName: String, earthDate: String): List<Photo> =
        apiDataSource.getPhotosByEarthDate(roverName = roverName, earthDate = earthDate)
            .map(PhotoDto::map)
}