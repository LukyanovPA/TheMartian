package com.pavellukyanov.themartian.domain.utils

import com.pavellukyanov.themartian.data.dto.Photo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface Storage<T> {
    suspend fun set(value: T)
    fun observe(): Flow<T>
}

class PhotoStorage : Storage<Photo?> {
    private val photo = MutableStateFlow<Photo?>(null)

    override suspend fun set(value: Photo?) {
        photo.emit(value)
    }

    override fun observe(): Flow<Photo?> = photo
}