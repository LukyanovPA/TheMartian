package com.pavellukyanov.themartian.data.cache.dao

import androidx.room.Dao
import androidx.room.Query
import com.pavellukyanov.themartian.data.dto.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao : BaseDao<Photo> {
    @Query("SELECT * FROM photo")
    fun observe(): Flow<List<Photo>>

    @Query("SELECT * FROM photo")
    suspend fun all(): List<Photo>

    @Query("SELECT * FROM photo WHERE id = :id")
    suspend fun getById(id: Int): Photo?
}