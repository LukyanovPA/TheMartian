package com.pavellukyanov.themartian.data.cache.dao

import androidx.room.Dao
import androidx.room.Query
import com.pavellukyanov.themartian.domain.entity.Rover
import kotlinx.coroutines.flow.Flow

@Dao
interface RoverInfoDao : BaseDao<Rover> {
    @Query("SELECT * FROM rover_info")
    fun subscribeAll(): Flow<List<Rover>>

    @Query("SELECT * FROM rover_info")
    suspend fun all(): List<Rover>

    @Query("DELETE FROM rover_info")
    suspend fun deleteAll()
}