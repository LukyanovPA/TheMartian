package com.pavellukyanov.themartian.data.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pavellukyanov.themartian.data.dto.Photo
import kotlinx.coroutines.flow.Flow

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photo")
    fun observe(): Flow<List<Photo>>

    @Query("SELECT * FROM photo")
    suspend fun all(): List<Photo>

    @Query("SELECT * FROM photo WHERE id = :id")
    suspend fun getById(id: Int): Photo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: Photo)

    @Delete
    suspend fun delete(photo: Photo)
}