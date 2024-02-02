package com.pavellukyanov.themartian.domain.utils

import com.pavellukyanov.themartian.data.dto.PhotoDto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface Storage<T> {
    suspend fun set(value: T)
    fun observe(): Flow<T>
}

class PhotoStorage : Storage<PhotoDto?> {
    private val photo = MutableStateFlow<PhotoDto?>(null)

    override suspend fun set(value: PhotoDto?) {
        photo.emit(value)
    }

    override fun observe(): Flow<PhotoDto?> = photo
}