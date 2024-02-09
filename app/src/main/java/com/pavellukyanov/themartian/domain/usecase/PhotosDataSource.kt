package com.pavellukyanov.themartian.domain.usecase

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.pavellukyanov.themartian.data.api.ApiDataSource
import com.pavellukyanov.themartian.data.cache.dao.PhotoDao
import com.pavellukyanov.themartian.data.dto.Photo
import com.pavellukyanov.themartian.domain.entity.PhotosOptions
import com.pavellukyanov.themartian.utils.ext.onCpu
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class PhotosDataSource(
    private val options: PhotosOptions,
    private val isLatest: Boolean,
    private val currentDate: (String) -> Unit
) : PagingSource<Int, Photo>(), KoinComponent {
    private val apiDataSource: ApiDataSource by inject()
    private val updateCamerasCache: UpdateCamerasCache by inject()

    //TODO сделать базу с пагинацией
    private val photoDao: PhotoDao by inject()

    override fun getRefreshKey(state: PagingState<Int, Photo>): Int? =
        state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Photo> =
        try {
            val currentPageNumber = params.key ?: 1

            val photos = if (isLatest)
                apiDataSource.getLatest(
                    roverName = options.roverName,
                    page = currentPageNumber
                )
            else
                apiDataSource.getPhotosByOptions(
                    options = options,
                    page = currentPageNumber
                )

            onCpu { updateCamerasCache(photos = photos) }

            currentDate(photos.first().earthDate)

            val nextKey = when (photos.size) {
                25 -> currentPageNumber + 1
                else -> null
            }

            LoadResult.Page(
                prevKey = null,
                nextKey = nextKey,
                data = photos
            )
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
}