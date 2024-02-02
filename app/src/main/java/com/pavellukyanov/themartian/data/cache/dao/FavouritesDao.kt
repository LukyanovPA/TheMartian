package com.pavellukyanov.themartian.data.cache.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.pavellukyanov.themartian.data.dto.PhotoDto
import kotlinx.coroutines.flow.Flow

@Dao
interface FavouritesDao {
    @Query("SELECT * FROM favourites")
    fun getAll(): Flow<List<PhotoDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoDto)

    @Delete
    suspend fun delete(photo: PhotoDto)
}