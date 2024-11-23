package com.pavellukyanov.themartian.data.cache.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import com.pavellukyanov.themartian.domain.entity.Camera
import kotlinx.coroutines.flow.Flow

@Dao
interface CamerasDao : BaseDao<Camera> {
    @Query("SELECT * FROM cameras WHERE name = :name")
    suspend fun getByName(name: String): List<Camera>

    @Query("SELECT * FROM cameras")
    fun observe(): Flow<List<Camera>>

    @Query("SELECT * FROM cameras")
    suspend fun all(): List<Camera>

    @Update(entity = Camera::class)
    suspend fun update(camera: Camera)

    @Query("DELETE FROM cameras")
    suspend fun deleteAll()
}